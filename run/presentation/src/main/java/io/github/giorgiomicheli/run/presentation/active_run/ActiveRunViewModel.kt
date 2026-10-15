package io.github.giorgiomicheli.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    // We already have a variable called "shouldTrack" in the ActiveRunState, but we also want
    // to react to changes of that variable.
    // The variable in the ActiveRunState is still the single source of truth, but we want to derive
    // flow out of it.
    // We need to use the 'snapshotFlow' function. It returns a cold flow where we can use Jetpack compose state.
    private val shouldTrack = snapshotFlow { state.shouldTrack }
        // We now convert the cold flow returned by "snapshotFlow" to a kotlin state flow.
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            state.shouldTrack
        )

    private val _hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        _hasLocationPermission
    ) {
        shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    init {
        _hasLocationPermission.onEach {hasPermission ->
            if (hasPermission) runningTracker.startObservingLocation()
            else runningTracker.stopObservingLocation()
        }
        .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        // All the computing logic lives is responsibility of the RunningTracker class
        // Here we just listen to the changes in order to update the screen ui state.
        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(currentLocation = it?.location)
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .onEach {
                state = state.copy(runData = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when(action) {
            is ActiveRunAction.OnBackClick -> {
                /*
                    When the user click the back button during an active run
                    we will just pause the run instead of going back to the previous screen.
                    The user can come back to the runOverview screen only if he finishes the run.
                    If the user hasn't started a run we of course allow him to go back and change screen.
                 */
                state = state.copy(shouldTrack = false)

                if (!state.hasStartedRunning) {
                    backButtonPressed()
                }
            }
            ActiveRunAction.OnFinishRunClick -> {

            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(shouldTrack = true)
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = action.acceptedLocationPermission

                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale
                )
            }
            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showNotificationRationale = false,
                    showLocationRationale = false
                )
            }
        }
    }

    private fun backButtonPressed() {
        viewModelScope.launch {
            eventChannel.send(ActiveRunEvent.BackButtonPressed)
        }
    }
}