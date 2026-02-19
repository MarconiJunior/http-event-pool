package br.com.marconi.engine

enum class ExecutionPolicy {
    ALLOW_CONCURRENT,
    IGNORE_IF_RUNNING,
    CANCEL_PREVIOUS,
    QUEUE
}
