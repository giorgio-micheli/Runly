package io.github.giorgiomicheli.run.domain

import io.github.giorgiomicheli.core.domain.Timer
import io.github.giorgiomicheli.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/*
    In this case we're tracking state in a singleton class.
    Process death can happen when the app goes in the background.
    The application state is not being restored in this case and we will loose everything.
    In a viewModel we can use saveStateHandle to restore the state, but in a normal class with
    don't have that mechanism. All of our run data will be resetted in that case.
    We still track this particular state in a normal class because in case of process death, it
    wouldn't be an issue to loose our tracking state.
    This because, we will only use this state in combination with a foreground service which will
    make sure that our app is still alive in the background.
    Even if the user closes the app from the recently used app tab, the process of the app will be
    kept alive. The viewModel will be killed but not the process.
 */
class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {
    // state for the UI, as always the first one is a private mutable object while the second one is
    // a public immutable one
    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    // This state flow is used to indicate that we're actively tracking a run
    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    // This state flow will instead indicate that we're tracking the user's location
    // We need two separate states because we want to update the position of the user on the map even
    // if he didn't press on "start tracking run"
    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if (isObserving) {
                locationObserver.observeLocation(1000L)
            } else flowOf()
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            null
        )

    // Hot flow about the geolocalization state
    // This hot flow starts emitting immediately (eagerly)
    val isGeolocalizationEnabled = locationObserver
        .observeGeolocalizationEnabled()
        .stateIn(
            applicationScope,
            SharingStarted.Eagerly,
            true
        )

    init {

        // Without geolocalization we can't track the run, so we pause it.
        // We never resume automatically, the user decides from the dialog.
        isGeolocalizationEnabled
            .onEach { isEnabled ->
                if (!isEnabled) setIsTracking(false)
            }
            .launchIn(applicationScope)

        // We only want to track the user's time only if isTracking is true and whenever it changes
        // we also want to stop if it's false
        _isTracking
            .onEach { isTracking ->
                if (!isTracking) {
                    val newList = buildList {
                        addAll(runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        it.copy(
                            locations = newList
                        )
                    }
                }

            }
            .flatMapLatest { isTracking ->
                if (isTracking) Timer.timeAndEmit()
                else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTracking) { location, isTracking ->
                if(isTracking) {
                    emit(location)
                }
            }
            // Read the current elapsed time when the location arrives. zip() paired each location
            // with a stale value (the previous point's time, or the last run's time after finishRun()).
            .map { location ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = _elapsedTime.value
                )
            }
            .onEach { location ->
                val currentLocations = runData.value.locations
                val lastLocationList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + location
                } else listOf(location)

                val newLocationList = currentLocations.replaceLast(lastLocationList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationList
                )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = location.durationTimestamp

                val averageSecondsPerKm = if (distanceKm == 0.0) 0
                else (currentDuration.inWholeSeconds / distanceKm).roundToInt()


                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = averageSecondsPerKm.seconds,
                        locations = newLocationList
                    )
                }
            }.launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }

    fun finishRun() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        _runData.value = RunData()
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    val new = listOf(replacement)
    return if (this.isEmpty()) new
    else this.dropLast(1) + new
}
