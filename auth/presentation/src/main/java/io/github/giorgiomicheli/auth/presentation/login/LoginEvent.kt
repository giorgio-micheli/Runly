package io.github.giorgiomicheli.auth.presentation.login

import io.github.giorgiomicheli.core.presentation.ui.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText): LoginEvent
    data object LoginSuccess : LoginEvent
    data object RegisterButtonClicked: LoginEvent
}