package io.github.giorgiomicheli.runly.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute: NavKey {
    @Serializable
    data object Intro: AppRoute
    @Serializable
    data object Login: AppRoute
    @Serializable
    data object Register: AppRoute
}