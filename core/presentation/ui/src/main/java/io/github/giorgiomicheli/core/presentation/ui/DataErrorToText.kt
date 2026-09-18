package io.github.giorgiomicheli.core.presentation.ui

import io.github.giorgiomicheli.core.domain.util.DataFailure

fun DataFailure.asUiText(): UiText {
    return when(this) {
        DataFailure.Local.DISK_FULL -> UiText.StringResource(
            R.string.failure_disk_full
        )
        DataFailure.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.failure_request_timeout
        )
        DataFailure.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.failure_too_many_requests
        )
        DataFailure.Network.NO_INTERNET -> UiText.StringResource(
            R.string.failure_no_internet
        )
        DataFailure.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.failure_payload_too_large
        )
        DataFailure.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.failure_server_error
        )
        DataFailure.Network.SERIALIZATION -> UiText.StringResource(
            R.string.failure_serialization
        )
        else -> UiText.StringResource(R.string.failure_unknown)
    }
}