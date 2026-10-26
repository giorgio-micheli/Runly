package io.github.giorgiomicheli.run.presentation.active_run

import io.github.giorgiomicheli.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data class Error(val error: UiText): ActiveRunEvent
    data object RunSaved: ActiveRunEvent
    data object BackButtonPressed: ActiveRunEvent
}