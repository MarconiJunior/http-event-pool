package br.com.marconi.state

data class HttpError(
    val message: String,
    val code: Int? = null,
    val cause: Throwable? = null,
    val payload: String? = null
)
