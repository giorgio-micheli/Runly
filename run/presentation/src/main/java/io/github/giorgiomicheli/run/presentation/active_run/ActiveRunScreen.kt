@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.giorgiomicheli.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.StartIcon
import io.github.giorgiomicheli.core.presentation.designsystem.StopIcon
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyDialog
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyFloatingActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyOutlinedActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyScaffold
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyToolbar
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.run.presentation.R
import io.github.giorgiomicheli.run.presentation.active_run.components.RunDataCard
import io.github.giorgiomicheli.run.presentation.util.hasLocationPermission
import io.github.giorgiomicheli.run.presentation.util.hasNotificationPermission
import io.github.giorgiomicheli.run.presentation.util.shouldShowLocationPermissionRationale
import io.github.giorgiomicheli.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ActiveRunEvent.BackButtonPressed -> onBackClick()
            else -> {}
        }
    }

    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    // true when the location has been declined permanently by the user
    // the only solution is to send the user to the app's settings
    var showSettingsDialog by remember { mutableStateOf(false) }

    /**
     * Legge lo stato reale dei permessi e lo comunica al ViewModel.
     * Usata sia dal launcher sia dal LaunchedEffect, così la regola è identica ovunque.
     *
     * @return true se la location è negata in modo permanente
     */
    fun submitPermissionInfo(): Boolean {
        // Checks both if the location and notification permissions are currently granted
        val hasLocation = context.hasLocationPermission()
        val hasNotification = context.hasNotificationPermission()
        // Checks if we should show the rationale dialog to inform the user about why we need those permissions
        // The rationale dialog needs to be shown only if the user already declined the permissions once
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        // We want the notification rationale to be present only if we also need to show the location's one
        val showNotificationRationale =
            showLocationRationale && activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasLocation,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotification,
                showNotificationRationale = showNotificationRationale
            )
        )

        return !hasLocation && !showLocationRationale
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Se abbiamo appena chiesto il permesso e non c'è né il permesso né il rationale,
        // significa che il rifiuto è permanente (o il popup non è nemmeno comparso).
        // Nota: leggiamo lo stato reale con le utility invece della mappa `perms`,
        // perché la mappa contiene solo i permessi che abbiamo effettivamente richiesto.
        showSettingsDialog = submitPermissionInfo()
    }

    LaunchedEffect(key1 = Unit) {
        val locationPermanentlyDenied = submitPermissionInfo()

        val showLocationRationale = activity.shouldShowLocationPermissionRationale()

        // Se il rationale è visibile aspettiamo il click su "Okay",
        // altrimenti richiediamo subito i permessi mancanti
        if (!showLocationRationale) {
            permissionLauncher.requestRunlyPermissions(context)
        }

        // Caso limite: se la location è già negata in modo permanente il launcher
        // risponde subito e la callback mostrerà il dialog; non serve fare altro qui.
        @Suppress("UNUSED_VARIABLE")
        val ignored = locationPermanentlyDenied
    }

    // When the user comes back from the app's settings we update the state
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (showSettingsDialog && context.hasLocationPermission()) {
            showSettingsDialog = false
            submitPermissionInfo()
        }
    }

    RunlyScaffold(
        withGradient = false,
        topAppBar = {
            RunlyToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                },
            )
        },
        floatingActionButton = {
            RunlyFloatingActionButton(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            RunDataCard(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth(),
                elapsedTime = state.elapsedTime,
                runData = state.runData,
            )
        }
    }

    // Rationale's dialog (single refuse)
    if (state.showLocationRationale || state.showNotificationRationale) {
        RunlyDialog(
            title = stringResource(R.string.permission_required),
            description = when {
                state.showLocationRationale && state.showNotificationRationale ->
                    stringResource(id = R.string.location_notification_rationale)
                state.showLocationRationale -> stringResource(id = R.string.location_rationale)
                else -> stringResource(id = R.string.notification_rationale)
            },
            primaryButton = {
                RunlyOutlinedActionButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissRationaleDialog)
                        permissionLauncher.requestRunlyPermissions(context)
                    }
                )
            },
            onDismiss = { },
        )
    }

    // Dialog per rifiuto permanente: porta alle impostazioni dell'app
    if (showSettingsDialog && !state.showLocationRationale) {
        RunlyDialog(
            title = stringResource(R.string.permission_required),
            description = stringResource(id = R.string.location_permanently_denied),
            primaryButton = {
                RunlyOutlinedActionButton(
                    text = stringResource(id = R.string.open_settings),
                    isLoading = false,
                    onClick = {
                        context.openAppSettings()
                    }
                )
            },
            onDismiss = { showSettingsDialog = false },
        )
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunlyPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermission = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }
        !hasLocationPermission -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermission)
    }
}

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunlyTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {}
        )
    }
}