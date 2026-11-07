package io.github.giorgiomicheli.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.awaitSnapshot
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import io.github.giorgiomicheli.core.domain.location.Location
import io.github.giorgiomicheli.core.domain.location.LocationTimestamp
import io.github.giorgiomicheli.core.presentation.designsystem.RunIcon
import io.github.giorgiomicheli.run.presentation.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val SNAPSHOT_TIMEOUT = 5.seconds

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackerMap(
    modifier: Modifier,
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    onSnapshot: (Bitmap?) -> Unit,
) {
    val context = LocalContext.current
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    val markerPositionLatitude by animateFloatAsState(
        targetValue = currentLocation?.latitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )
    val markerPositionLongitude by animateFloatAsState(
        targetValue = currentLocation?.longitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    val markerPosition = remember(markerPositionLatitude, markerPositionLongitude) {
        LatLng(markerPositionLatitude.toDouble(), markerPositionLongitude.toDouble())
    }

    /*
    Listen to the changes of markerPosition and isRunFinished and everytime check if the run has
    finished. If it hasn't then update the position
     */
    LaunchedEffect(markerPosition, isRunFinished) {
        if (!isRunFinished) {
            markerState.position = markerPosition
        }
    }
    // Animate the reposition of the camera everytime that the position change
    LaunchedEffect(currentLocation, isRunFinished) {
        if(currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 16.7f)
            )
        }
    }

    // Capture the map's snapshot when true
    var triggerCapture by remember { mutableStateOf(false) }

    var hasDeliveredSnapshot by remember { mutableStateOf(false) }

    val snapshotJob = remember { mutableStateOf<Job?>(null) }

    val scope = rememberCoroutineScope()

    val currentOnSnapshot by rememberUpdatedState(onSnapshot)

    val currentRunIsFinished by rememberUpdatedState(isRunFinished)

    fun deliverSnapshot(bitmap: Bitmap?) {
        if (hasDeliveredSnapshot || !currentRunIsFinished) return
        hasDeliveredSnapshot = true
        snapshotJob.value?.cancel()
        currentOnSnapshot(bitmap)
    }

    /*
    Safety net: whatever happens (the camera idle listener never fires, awaitSnapshot() hangs or
    returns null) after SNAPSHOT_TIMEOUT the caller gets an answer, so the loading can't spin forever.
    If the real snapshot was already delivered, deliverSnapshot() ignores this call.
     */
    LaunchedEffect(isRunFinished) {
        if (isRunFinished) {
            delay(SNAPSHOT_TIMEOUT)
            deliverSnapshot(null)
        } else {
            // New run (after an invalid one): reset the snapshot state
            hasDeliveredSnapshot = false
            triggerCapture = false
            snapshotJob.value?.cancel()
            snapshotJob.value = null
        }
    }

    GoogleMap(
        modifier = if (isRunFinished) {
            modifier
                .width(300.dp)
                .aspectRatio(16/9f)
                .alpha(0f)
                .onSizeChanged {
                    if (it.width >= 300) triggerCapture = true
                }
        } else modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyle),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {

        RunlyPolylines(locations = locations)

        MapEffect(locations, isRunFinished, triggerCapture) { map ->
            // 1) Run not finished: make sure no listener from a previous run is left behind
            if (!isRunFinished) {
                map.setOnCameraIdleListener(null)
                return@MapEffect
            }

            if (!triggerCapture || hasDeliveredSnapshot) return@MapEffect
            triggerCapture = false

            val points = locations.flatten()
            // LatLngBounds.build() throws with no points: there's no map to capture, answer right away
            if (points.isEmpty()) {
                deliverSnapshot(null)
                return@MapEffect
            }

            // Register the listener BEFORE moving the camera, so the "idle" event can't be lost
            map.setOnCameraIdleListener {
                // 2) Extra safety: if the snapshot was already delivered, do nothing
                if (hasDeliveredSnapshot || !currentRunIsFinished) return@setOnCameraIdleListener
                snapshotJob.value?.cancel()
                snapshotJob.value = scope.launch {
                    // We wait just to be sure that the map is focused and rendered properly
                    // before taking the screenshot
                    delay(500L.milliseconds)
                    val bitmap = map.awaitSnapshot()
                    map.setOnCameraIdleListener(null)
                    // It may be null: the caller falls back to the placeholder image
                    deliverSnapshot(bitmap)
                }
            }

            val boundsBuilder = LatLngBounds.builder()
            points.forEach { location ->
                boundsBuilder.include(
                    LatLng(
                        location.location.location.latitude,
                        location.location.location.longitude
                    )
                )
            }
            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    100
                )
            )
        }

        if (!isRunFinished && currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state = markerState
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(20.dp),
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}