package br.com.marconi.state

sealed interface HttpState<out T> {
    data object Idle : HttpState<Nothing>
    data object Loading : HttpState<Nothing>
    data class Success<T>(val value: T) : HttpState<T>
    data class Failure(val error: HttpError) : HttpState<Nothing>
}
