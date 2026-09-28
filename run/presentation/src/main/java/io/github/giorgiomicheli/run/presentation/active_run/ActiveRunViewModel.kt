package io.github.giorgiomicheli.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ActiveRunViewModel : ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    fun onAction(action: ActiveRunAction) {
        when(action) {
            is ActiveRunAction.OnBackClick -> backButtonPressed()
            ActiveRunAction.OnFinishRunClick -> {}
            ActiveRunAction.OnResumeRunClick -> {}
            ActiveRunAction.OnToggleRunClick -> {}
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