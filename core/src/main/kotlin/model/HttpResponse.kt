package br.com.marconi.model

data class HttpResponse(
    val code: Int,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    val isSuccess: Boolean = code in 200..299
}
