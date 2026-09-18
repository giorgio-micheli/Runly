package io.github.giorgiomicheli.core.domain.util

sealed interface DataFailure : Failure {
    enum class Network: DataFailure {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN
    }
    enum class Local: DataFailure {
        DISK_FULL
    }
}