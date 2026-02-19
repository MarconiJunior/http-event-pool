package br.com.marconi.event

import br.com.marconi.engine.ExecutionPolicy
import br.com.marconi.model.HttpRequest
import br.com.marconi.model.HttpResponse
import br.com.marconi.state.HttpError
import br.com.marconi.state.HttpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class HttpEvent<P : Any, R : Any>(
    val key: String,
    val policy: ExecutionPolicy = ExecutionPolicy.CANCEL_PREVIOUS
) {
    private val mutableState = MutableStateFlow<HttpState<R>>(HttpState.Idle)

    val state: StateFlow<HttpState<R>> = mutableState.asStateFlow()

    internal fun emit(next: HttpState<R>) {
        mutableState.value = next
    }

    abstract suspend fun buildRequest(params: P): HttpRequest

    abstract fun parseResponse(response: HttpResponse): R

    open fun mapFailure(throwable: Throwable): HttpError {
        return HttpError(
            message = throwable.message ?: "Unknown network error",
            cause = throwable
        )
    }
}
