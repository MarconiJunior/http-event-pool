package br.com.marconi.sample

import br.com.marconi.annotations.HttpEndpoint
import br.com.marconi.annotations.HttpExecution
import br.com.marconi.client.HttpClientAdapter
import br.com.marconi.engine.ExecutionPolicy
import br.com.marconi.engine.HttpEngine
import br.com.marconi.event.HttpEvent
import br.com.marconi.model.HttpRequest
import br.com.marconi.model.HttpResponse
import br.com.marconi.state.HttpState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TodoResponse(
    val id: Long,
    val title: String,
    val completed: Boolean
)

@HttpEndpoint(method = "GET", path = "/todos/1")
@HttpExecution(
    key = "get-todo",
    policy = "IGNORE_IF_RUNNING",
    serializable = true
)
class GetTodoEvent : HttpEvent<Unit, TodoResponse>(
    key = "get-todo",
    policy = ExecutionPolicy.IGNORE_IF_RUNNING
) {
    override suspend fun buildRequest(params: Unit): HttpRequest {
        return HttpRequest(
            method = "GET",
            url = "https://jsonplaceholder.typicode.com/todos/1"
        )
    }

    override fun parseResponse(response: HttpResponse): TodoResponse {
        return Json.decodeFromString(response.body ?: error("Empty response body"))
    }
}

fun main(): Unit = runBlocking {
    val fakeAdapter = HttpClientAdapter {
        HttpResponse(
            code = 200,
            body = """{"id":1,"title":"Study HttpEventPool","completed":false}"""
        )
    }

    val engine = HttpEngine(adapter = fakeAdapter, scope = this)
    val event = GetTodoEvent()

    engine.dispatch(event, Unit)
    delay(20)

    when (val state = event.state.value) {
        is HttpState.Success -> println("Success: ${state.value}")
        is HttpState.Failure -> println("Failure: ${state.error.message}")
        HttpState.Idle -> println("Idle")
        HttpState.Loading -> println("Loading")
    }
}
