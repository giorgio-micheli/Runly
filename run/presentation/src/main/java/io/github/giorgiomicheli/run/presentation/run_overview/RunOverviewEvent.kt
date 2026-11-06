package io.github.giorgiomicheli.run.presentation.run_overview

/*
    One time events that are fired in response of a user action in the RunOveview screen
 */
sealed interface RunOverviewEvent {
    data object ActiveRunButtonClicked : RunOverviewEvent
    data object LogoutButtonClicked : RunOverviewEvent
}