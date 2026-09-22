package io.github.giorgiomicheli.auth.presentation.register

import io.github.giorgiomicheli.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegistrationSuccess: RegisterEvent
    data class Failure(val error: UiText): RegisterEvent
}