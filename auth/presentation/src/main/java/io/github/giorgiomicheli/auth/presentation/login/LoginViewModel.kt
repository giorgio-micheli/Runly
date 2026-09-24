package io.github.giorgiomicheli.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.auth.domain.AuthRepository
import io.github.giorgiomicheli.auth.domain.UserDataValidator
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.Result
import io.github.giorgiomicheli.core.presentation.ui.R
import io.github.giorgiomicheli.core.presentation.ui.UiText
import io.github.giorgiomicheli.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
): ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        combine(snapshotFlow { state.email.text }, snapshotFlow { state.password.text }) { email, password ->
            state = state.copy(
                canLogin = userDataValidator.isValidEmail(email.toString().trim())
                        && password.isNotEmpty()
            )
        }.launchIn(viewModelScope)
    }
    fun onAction(action: LoginAction) {
        when(action) {
            is LoginAction.OnRegisterClick -> registerButtonClicked()
            is LoginAction.OnLoginClick -> login()
            is LoginAction.OnTogglePasswordVisibility -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLogginIn = true)
            val result = authRepository.login(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString()
            )
            state = state.copy(isLogginIn = false)

            when(result) {
                is Result.Failure -> {
                    if (result.failure == DataFailure.Network.UNAUTHORIZED) {
                        eventChannel.send(LoginEvent.Error(
                            UiText.StringResource(R.string.error_email_password_incorrect)
                        ))
                    } else {
                        eventChannel.send(LoginEvent.Error(result.failure.asUiText()))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }

    private fun registerButtonClicked() {
        viewModelScope.launch {
            eventChannel.send(LoginEvent.RegisterButtonClicked)
        }
    }




}