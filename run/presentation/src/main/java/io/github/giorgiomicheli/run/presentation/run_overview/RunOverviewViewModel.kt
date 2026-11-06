package io.github.giorgiomicheli.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.core.domain.SessionStorage
import io.github.giorgiomicheli.core.domain.run.RunRepository
import io.github.giorgiomicheli.core.domain.run.SyncRunScheduler
import io.github.giorgiomicheli.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        // Start fetching runs from the remote server after 30 minutes
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
            )
        }
        /*
         We collect this cold flow in the viewModelScop.
         Whenever the runs table is updated this a new list of runs will arrive and
         will be displayed, until the viewModel exist in memory.
         */
        // onEach intermediate flow operator
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        // executed only one time, it's not a flow
        viewModelScope.launch {
            /*
            Only after syncing the pending runs that we still have locally
            we know that the backend is up-to-date.
            So the runs that we fetch after the sync reflect the real runs.
             */
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    private val eventChannel = Channel<RunOverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: RunOverviewAction) {
        when (action) {
            is RunOverviewAction.OnStartClick -> onStartButtonClicked()
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            is RunOverviewAction.OnLogoutClick -> {
                logout()
                viewModelScope.launch {
                    eventChannel.send(RunOverviewEvent.LogoutButtonClicked)
                }
            }
            else -> {}
        }
    }

    private fun onStartButtonClicked() {
        viewModelScope.launch {
            eventChannel.send(RunOverviewEvent.ActiveRunButtonClicked)
        }
    }

    private fun logout() {
        applicationScope.launch {
            syncRunScheduler.cancelAllSyncs()
            runRepository.deleteAllRuns()
            // Invalidates the user's token remotely
            runRepository.logout()
            sessionStorage.set(null)
        }
    }

}