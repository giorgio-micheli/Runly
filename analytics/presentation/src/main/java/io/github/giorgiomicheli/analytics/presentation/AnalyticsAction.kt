package io.github.giorgiomicheli.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick: AnalyticsAction
}