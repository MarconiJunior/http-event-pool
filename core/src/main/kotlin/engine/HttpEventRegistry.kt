package br.com.marconi.engine

import br.com.marconi.annotations.HttpEndpoint
import br.com.marconi.annotations.HttpExecution
import br.com.marconi.event.HttpEvent

class HttpEventRegistry(
    events: List<HttpEvent<*, *>>
) {
    private val byKey: Map<String, HttpEvent<*, *>> = events.associateBy { event ->
        val execution = event.javaClass.getAnnotation(HttpExecution::class.java)
        execution?.key?.takeIf { it.isNotBlank() } ?: event.key
    }

    fun get(key: String): HttpEvent<*, *>? = byKey[key]

    fun all(): List<HttpEvent<*, *>> = byKey.values.toList()

    fun describe(key: String): EventDescription? {
        val event = byKey[key] ?: return null
        val endpoint = event.javaClass.getAnnotation(HttpEndpoint::class.java)
        val execution = event.javaClass.getAnnotation(HttpExecution::class.java)

        return EventDescription(
            key = key,
            method = endpoint?.method,
            path = endpoint?.path,
            policy = execution?.policy ?: event.policy.name,
            serializable = execution?.serializable ?: true,
            pollingEnabled = execution?.polling?.enabled ?: false,
            pollingIntervalMs = execution?.polling?.intervalMs ?: 0L
        )
    }
}

data class EventDescription(
    val key: String,
    val method: String?,
    val path: String?,
    val policy: String,
    val serializable: Boolean,
    val pollingEnabled: Boolean,
    val pollingIntervalMs: Long
)
