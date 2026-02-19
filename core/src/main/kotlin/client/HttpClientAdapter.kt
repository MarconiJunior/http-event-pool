package br.com.marconi.client

import br.com.marconi.model.HttpRequest
import br.com.marconi.model.HttpResponse

fun interface HttpClientAdapter {
    suspend fun execute(request: HttpRequest): HttpResponse
}
