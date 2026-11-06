@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.giorgiomicheli.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
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
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyDialog
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyFloatingActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyOutlinedActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyScaffold
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyToolbar
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.run.presentation.R
import io.github.giorgiomicheli.run.presentation.active_run.components.LocationWarning
import io.github.giorgiomicheli.run.presentation.active_run.components.RunDataCard
import io.github.giorgiomicheli.run.presentation.active_run.maps.TrackerMap
import io.github.giorgiomicheli.run.presentation.active_run.service.ActiveRunService
import io.github.giorgiomicheli.run.presentation.util.hasLocationPermission
import io.github.giorgiomicheli.run.presentation.util.hasNotificationPermission
import io.github.giorgiomicheli.run.presentation.util.shouldShowLocationPermissionRationale
import io.github.giorgiomicheli.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ActiveRunEvent.BackButtonPressed -> onBackClick()
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            is ActiveRunEvent.RunSaved -> onFinish()
        }
    }

    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    // true when the location has been declined permanently by the user
    // the only solution is to send the user to the app's settings
    var showSettingsDialog by remember { mutableStateOf(false) }

    /**
     * Reads the real state of the permissions and send them to the viewModel
     * Used both by the launcher and the LaunchedEffect, this way the rule is the same everywhere.
     *
     * @return true if the location permission has been declined permanently
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
        /*
        If we just asked for the permission e the permission and the rationale are not present,
        this means that the user decided to permanently decline the permission.
        We read the real state usign this utility function instead of the map "perms" cause the map
        only contains the permissions that we actually required.
         */
        showSettingsDialog = submitPermissionInfo()
    }

    LaunchedEffect(key1 = Unit) {
        submitPermissionInfo()
        //val locationPermanentlyDenied = submitPermissionInfo()

        val showLocationRationale = activity.shouldShowLocationPermissionRationale()

        // If the rationale message is visible we wait for the user click on "Okay" to show again
        // the android permission dialog. Otherwise, we directly request again the permissions
        if (!showLocationRationale) {
            permissionLauncher.requestRunlyPermissions(context)
        }
    }
    // When the user comes back from the app's settings we update the state
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (showSettingsDialog && context.hasLocationPermission()) {
            showSettingsDialog = false
            submitPermissionInfo()
        }
    }

    // When we start tracking the run we also start the foreground service
    LaunchedEffect(key1 = state.shouldTrack) {
        if (context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    // If the run is finished we stop the foreground service
    LaunchedEffect(key1 = state.isRunFinished) {
        if (state.isRunFinished) onServiceToggle(false)
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
                isRunning = state.shouldTrack,
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
            TrackerMap(
                modifier = Modifier
                    .fillMaxSize(),
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bitmap ->
                    val imageByteArray = if (bitmap != null) {
                        val stream = ByteArrayOutputStream()
                        stream.use {
                            bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                80,
                                it
                            )
                        }
                        stream.toByteArray()
                    } else {
                        context.resources.openRawResource(R.raw.map_placeholder).use { it.readBytes() }
                    }

                    onAction(ActiveRunAction.OnRunProcessed(imageByteArray))
                }
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RunDataCard(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .padding(padding)
                        .fillMaxWidth(),
                    elapsedTime = state.elapsedTime,
                    runData = state.runData,
                )
                LocationWarning()
            }

        }
    }

    if (!state.shouldTrack && state.hasStartedRunning) {
        RunlyDialog(
            title = stringResource(id = R.string.running_is_paused),
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRunClick)
            },
            description = stringResource(R.string.resume_or_finish_run),
            primaryButton = {
                RunlyActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.resume),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRunClick)
                    }
                )
            },
            secondaryButton = {
                RunlyOutlinedActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    }
                )
            }
        )
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

    // Dialog for informing the user that he declined permanently the location permission,
    // By clicking on "Open settings" the user will be taken to the app's settings
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
            onServiceToggle = {},
            onAction = {}
        )
    }
}