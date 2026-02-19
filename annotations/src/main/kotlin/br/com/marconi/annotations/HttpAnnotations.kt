package br.com.marconi.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HttpEndpoint(
    val method: String,
    val path: String
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HttpExecution(
    val key: String = "",
    val policy: String = "CANCEL_PREVIOUS",
    val serializable: Boolean = true,
    val polling: Polling = Polling()
)

@Retention(AnnotationRetention.RUNTIME)
annotation class Polling(
    val enabled: Boolean = false,
    val intervalMs: Long = 0L
)
