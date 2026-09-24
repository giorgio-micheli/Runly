package io.github.giorgiomicheli.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.auth.domain.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when(action) {
            is LoginAction.OnRegisterClick -> registerButtonClicked()
            is LoginAction.OnLoginClick -> login()
            else -> {}
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLogginIn = true)
            delay(1.seconds)
            state = state.copy(isLogginIn = false)
        }
    }

    private fun registerButtonClicked() {
        viewModelScope.launch {
            eventChannel.send(LoginEvent.RegisterButtonClicked)
        }
    }




}