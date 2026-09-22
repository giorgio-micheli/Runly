package io.github.giorgiomicheli.runly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.giorgiomicheli.auth.presentation.intro.IntroScreenRoot
import io.github.giorgiomicheli.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(AppRoute.Intro)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Intro> {
                IntroScreenRoot(
                    onSignUpClick = {
                        backStack.add(AppRoute.Register)
                    },
                    onSignInClick = {
                        backStack.add(AppRoute.Login)
                    }
                )
            }
            entry<AppRoute.Register> {
                RegisterScreenRoot(
                    onSignInClick = {
                        backStack.add(AppRoute.Login)
                    },
                    onSuccessFulRegistration = {
                        backStack.add(AppRoute.Login)
                    }
                )
            }
            entry<AppRoute.Login> {
                //TODO: add the right composable
            }
        }
    )
}