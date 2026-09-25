package io.github.giorgiomicheli.run.presentation.run_overview

sealed interface RunOverviewEvent {
    data object ActiveRunButtonClicked : RunOverviewEvent
}