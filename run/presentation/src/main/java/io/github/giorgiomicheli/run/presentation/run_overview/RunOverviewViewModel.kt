package io.github.giorgiomicheli.run.presentation.run_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.run.presentation.active_run.ActiveRunEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RunOverviewViewModel : ViewModel() {

    private val eventChannel = Channel<RunOverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: RunOverviewAction) {
        when (action) {
            is RunOverviewAction.OnStartClick -> onStartButtonClicked()
            else -> {}
        }
    }

    private fun onStartButtonClicked() {
        viewModelScope.launch {
            eventChannel.send(RunOverviewEvent.ActiveRunButtonClicked)
        }
    }

}