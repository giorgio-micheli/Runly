package io.github.giorgiomicheli.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ActiveRunViewModel : ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ActiveRunAction) {
        when(action) {
            is ActiveRunAction.OnBackClick -> backButtonPressed()
            else -> {}
        }
    }

    private fun backButtonPressed() {
        viewModelScope.launch {
            eventChannel.send(ActiveRunEvent.BackButtonPressed)
        }
    }
}