package br.com.marconi.compose

import br.com.marconi.event.HttpEvent
import br.com.marconi.state.HttpState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object HttpEventUiBinding {
    fun <R : Any> stateFlowOf(event: HttpEvent<*, R>): StateFlow<HttpState<R>> = event.state

    fun <R : Any> observe(
        scope: CoroutineScope,
        event: HttpEvent<*, R>,
        onStateChanged: (HttpState<R>) -> Unit
    ): Job {
        return scope.launch {
            event.state.collect(onStateChanged)
        }
    }
}
