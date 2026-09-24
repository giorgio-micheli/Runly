package io.github.giorgiomicheli.runly

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.core.domain.SessionStorage
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingAuth = true)
            // We're only checking if we ever logged in once, not so secure in my opinion
            val isLoggedIn = sessionStorage.get() != null
            state = state.copy(
                isLoggedIn = isLoggedIn
            )
            state = state.copy(isCheckingAuth = false)
        }
    }
}