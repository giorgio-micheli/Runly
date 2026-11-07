package io.github.giorgiomicheli.runly.navigation

import android.content.Intent
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest

import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import kotlinx.serialization.serializer

private val activeRunMatcher = UriDeepLinkMatcher(
    DeepLinkUri("runly://active_run"),
    serializer<AppRoute.ActiveRun>()
).withBackStack { matchResult ->
    // We put the RunOverviewScreen under the ActiveRun navKey so when the user presses the back button
    // goes back to the RunOverviewScreen instead of leaving the app.
    // I temporarely removed this option cause if the user is already on the home screen and then a deeplink arrives,
    // there will be two homeScreens on the backstack.
    listOf(AppRoute.RunOverview, matchResult.key)
}

private val deepLinkMatchers: List<DeepLinkMatcher<*, *>> = listOf(activeRunMatcher)

/** Ritorna il back stack per il deep link, o null se l'intent non è un deep link riconosciuto. */
fun deepLinkBackStackOrNull(intent: Intent?): List<NavKey>? {
    if (intent?.data == null) return null // standard launch from the launcher

    val matchResult = deepLinkMatchers
        .mapNotNull { it.match(DeepLinkRequest(uri = intent.data)) }
        .maxOrNull() ?: return null

    return when (matchResult) {
        is BackStackMatchResult<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            matchResult.backStack as List<NavKey>
        }
        else -> listOf(matchResult.key as NavKey)
    }
}