package io.github.giorgiomicheli.run.presentation.run_overview

/*
    Actions that a user can do on the RunOverview screen
*/

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnLogoutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
}