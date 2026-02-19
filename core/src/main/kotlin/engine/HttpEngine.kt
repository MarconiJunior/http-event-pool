package br.com.marconi.engine

import br.com.marconi.client.HttpClientAdapter
import br.com.marconi.event.HttpEvent
import br.com.marconi.state.HttpError
import br.com.marconi.state.HttpState
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HttpEngine(
    private val adapter: HttpClientAdapter,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val runningJobs = ConcurrentHashMap<String, Job>()
    private val queueLocks = ConcurrentHashMap<String, Mutex>()

    fun <P : Any, R : Any> dispatch(event: HttpEvent<P, R>, params: P) {
        when (event.policy) {
            ExecutionPolicy.ALLOW_CONCURRENT -> launchExecution(event, params)
            ExecutionPolicy.IGNORE_IF_RUNNING -> {
                if (runningJobs[event.key]?.isActive != true) {
                    launchExecution(event, params)
                }
            }
            ExecutionPolicy.CANCEL_PREVIOUS -> {
                runningJobs[event.key]?.cancel()
                launchExecution(event, params)
            }
            ExecutionPolicy.QUEUE -> {
                val lock = queueLocks.computeIfAbsent(event.key) { Mutex() }
                launchExecution(event, params, lock)
            }
        }
    }

    fun cancel(key: String) {
        runningJobs[key]?.cancel()
        runningJobs.remove(key)
    }

    private fun <P : Any, R : Any> launchExecution(
        event: HttpEvent<P, R>,
        params: P,
        lock: Mutex? = null
    ) {
        val job = scope.launch {
            if (lock != null) {
                lock.withLock {
                    runEvent(event, params)
                }
            } else {
                runEvent(event, params)
            }
        }

        runningJobs[event.key] = job
        job.invokeOnCompletion {
            if (runningJobs[event.key] == job) {
                runningJobs.remove(event.key)
            }
        }
    }

    private suspend fun <P : Any, R : Any> runEvent(event: HttpEvent<P, R>, params: P) {
        event.emit(HttpState.Loading)
        try {
            val request = event.buildRequest(params)
            val response = adapter.execute(request)
            if (!response.isSuccess) {
                event.emit(
                    HttpState.Failure(
                        HttpError(
                            message = "Request failed with HTTP ${response.code}",
                            code = response.code,
                            payload = response.body
                        )
                    )
                )
                return
            }

            val parsed = event.parseResponse(response)
            event.emit(HttpState.Success(parsed))
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                return
            }
            event.emit(HttpState.Failure(event.mapFailure(throwable)))
        }
    }
}
