package io.github.giorgiomicheli.run.domain

import io.github.giorgiomicheli.core.domain.Timer
import io.github.giorgiomicheli.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
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
    private val isTracking = MutableStateFlow(false)
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

    init {
        // We only want to track the user's time only if isTracking is true and whenever it changes
        // we also want to stop if it's false
        isTracking
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
            .combineTransform(isTracking) { location, isTracking ->
                if(isTracking) {
                    emit(location)
                }
            }
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
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
        this.isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    val new = listOf(replacement)
    return if (this.isEmpty()) new
    else this.dropLast(1) + new
}
