package io.github.giorgiomicheli.run.presentation.active_run

/*
    All the actions that the user can do on the ActiveRunScreen
 */
sealed interface ActiveRunAction {
    data object OnToggleRunClick: ActiveRunAction
    data object OnFinishRunClick: ActiveRunAction
    data object OnResumeRunClick: ActiveRunAction
    data object OnBackClick: ActiveRunAction
}