package io.github.giorgiomicheli.runly.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.giorgiomicheli.auth.presentation.intro.IntroScreenRoot
import io.github.giorgiomicheli.auth.presentation.login.LoginScreenRoot
import io.github.giorgiomicheli.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(AppRoute.Intro)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Intro> {
                IntroScreenRoot(
                    onSignUpClick = {
                        backStack.add(AppRoute.Register)
                        backStack.printCurrentBackstack()
                    },
                    onSignInClick = {
                        backStack.add(AppRoute.Login)
                        backStack.printCurrentBackstack()
                    }
                )
            }
            entry<AppRoute.Register> {
                RegisterScreenRoot(
                    onSignInClick = {
                        backStack.removeLastOrNull()
                        backStack.add(AppRoute.Login)
                        backStack.printCurrentBackstack()
                    },
                    onSuccessfulRegistration = {
                        backStack.clear()
                        backStack.add(AppRoute.Login)
                    },
                )
            }
            entry<AppRoute.Login> {
                LoginScreenRoot(
                    onSignUpClick = {
                        backStack.removeLastOrNull()
                        backStack.add(AppRoute.Register)
                        backStack.printCurrentBackstack()
                    },
                    onLoginSuccessful = {
                        backStack.clear()
                        backStack.add(AppRoute.Run)
                    },
                )
            }
            entry<AppRoute.Run> {
                Text(
                    text = "Run screen"
                )
            }
        }
    )
}