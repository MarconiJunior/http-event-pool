package br.com.marconi.retrofit

import br.com.marconi.client.HttpClientAdapter
import br.com.marconi.model.HttpRequest
import br.com.marconi.model.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class RetrofitHttpClientAdapter(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val defaultHeaders: Map<String, String> = emptyMap()
) : HttpClientAdapter {
    override suspend fun execute(request: HttpRequest): HttpResponse = withContext(Dispatchers.IO) {
        val urlWithQuery = appendQuery(request.url, request.query)
        val requestBuilder = Request.Builder().url(urlWithQuery)

        defaultHeaders.forEach { (key, value) ->
            requestBuilder.header(key, value)
        }
        request.headers.forEach { (key, value) ->
            requestBuilder.header(key, value)
        }

        val body = request.body?.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        requestBuilder.method(request.method.uppercase(), body)

        val response = okHttpClient.newCall(requestBuilder.build()).execute()
        val responseBody = response.body?.string()
        val headers = response.headers.toMultimap().mapValues { (_, values) -> values.joinToString(",") }

        HttpResponse(
            code = response.code,
            headers = headers,
            body = responseBody
        )
    }

    private fun appendQuery(url: String, query: Map<String, String>): String {
        if (query.isEmpty()) {
            return url
        }

        val separator = if (url.contains("?")) "&" else "?"
        val queryString = query.entries.joinToString("&") { (key, value) ->
            "${key.urlEncode()}=${value.urlEncode()}"
        }
        return "$url$separator$queryString"
    }

    private fun String.urlEncode(): String {
        return java.net.URLEncoder.encode(this, Charsets.UTF_8.name())
    }
}
