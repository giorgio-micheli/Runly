package io.github.giorgiomicheli.runly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(AppRoute.Intro)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Intro> {
                //TODO: add the right composable
            }
            entry<AppRoute.Login> {
                //TODO: add the right composable
            }
            entry<AppRoute.Register> {
                //TODO: add the right composable
            }
        }
    )
}