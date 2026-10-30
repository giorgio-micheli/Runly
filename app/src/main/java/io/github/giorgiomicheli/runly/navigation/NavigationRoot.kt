package io.github.giorgiomicheli.runly.navigation

import android.content.Intent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.giorgiomicheli.auth.presentation.intro.IntroScreenRoot
import io.github.giorgiomicheli.auth.presentation.login.LoginScreenRoot
import io.github.giorgiomicheli.auth.presentation.register.RegisterScreenRoot
import io.github.giorgiomicheli.run.presentation.active_run.ActiveRunScreenRoot
import io.github.giorgiomicheli.run.presentation.active_run.service.ActiveRunService
import io.github.giorgiomicheli.run.presentation.run_overview.RunOverviewScreenRoot
import io.github.giorgiomicheli.runly.MainActivity

val durata = 400

/**
 * @param launchIntent the link that the app was launched with. If it contains a valid link then
 * we start from there, otherwise we start from the homeScreen as usual, based on if the user is already logged in or not.
 * @param newIntent a link that arrived when the app was already opened.
 * @param onNewIntentConsumed what to do after the newIntent has been consumed. We reset the variable in the mainActivity to null.
 */
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    launchIntent: Intent? = null,
    newIntent: Intent? = null,
    onNewIntentConsumed: () -> Unit = {}
    ) {

    val initialStack: List<NavKey> = if (isLoggedIn) {
        deepLinkBackStackOrNull(launchIntent) ?: listOf(AppRoute.RunOverview)
    } else listOf(AppRoute.Intro)

    val backStack = rememberNavBackStack(*initialStack.toTypedArray())

    // Everytime that 'newIntent' changes this LaunchedEffect runs
    LaunchedEffect(newIntent) {
        if (newIntent == null) return@LaunchedEffect
        if (isLoggedIn) {
            deepLinkBackStackOrNull(newIntent)?.let { stack ->
                backStack.clear()
                backStack.addAll(stack)
            }
        }
        onNewIntentConsumed()
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = {
            fadeIn(tween(durata)) togetherWith
                    fadeOut(tween(durata))
        },
        popTransitionSpec = {
            fadeIn(tween(durata)) togetherWith
                    fadeOut(tween(durata))
        },
        predictivePopTransitionSpec = {
            fadeIn(tween(durata)) togetherWith
                    fadeOut(tween(durata))
        },
        entryProvider = entryProvider {
            entry<AppRoute.Intro> {
                IntroScreenRoot(
                    onSignUpClick = {
                        backStack.add(AppRoute.Register)
                        //backStack.printCurrentBackstack()
                    },
                    onSignInClick = {
                        backStack.add(AppRoute.Login)
                        //backStack.printCurrentBackstack()
                    }
                )
            }
            entry<AppRoute.Register> {
                RegisterScreenRoot(
                    onSignInClick = {
                        backStack.removeLastOrNull()
                        backStack.add(AppRoute.Login)
                        //backStack.printCurrentBackstack()
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
                        //backStack.printCurrentBackstack()
                    },
                    onLoginSuccessful = {
                        backStack.clear()
                        backStack.add(AppRoute.RunOverview)
                    },
                )
            }
            entry<AppRoute.RunOverview> {
                RunOverviewScreenRoot(
                    onActiveRunButtonClicked = {
                        backStack.add(AppRoute.ActiveRun)
                    }
                )
            }
            entry<AppRoute.ActiveRun> {
                val context = LocalContext.current
                ActiveRunScreenRoot(
                    onServiceToggle = {shouldServiceRun ->
                        if (shouldServiceRun) {
                            context.startService(
                                ActiveRunService.createStartIntent(
                                    context = context,
                                    activityClass = MainActivity::class.java
                                )
                            )
                        } else {
                            /*
                            Here it's a little bit confusing, we still use the startService but
                            we're actually passing an intent that will be interpreted like a stop
                            command.
                             */
                            context.startService(
                                ActiveRunService.createStopIntent(context)
                            )
                        }

                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    },
                    onFinish = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}