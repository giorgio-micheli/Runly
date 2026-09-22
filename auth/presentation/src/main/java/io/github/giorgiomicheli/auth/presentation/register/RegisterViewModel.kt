package io.github.giorgiomicheli.auth.presentation.register

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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val repository: AuthRepository,
) : ViewModel() {

    /*
    We use state flows only if we need reactivity.
    If we just need to expose the state to the UI then compose state is perfectly ok.
    Even when we use state flows in the end we turn them into compose state.
     */
    var state by mutableStateOf(RegisterState())
        private set

    /*
    An event is considered as something being sent back from the viewModel to the UI
    (Toast, Error notifications)
     */
    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        snapshotFlow { state.email.text }
            .onEach { email ->
                val isValidEmail = userDataValidator.isValidEmail(email.toString())
                state = state.copy(
                    isEmailValid = isValidEmail,
                    canRegister = isValidEmail && state.passwordValidationState.isValidPassword
                            && !state.isRegistering
                )
            }
            .launchIn(viewModelScope)

        snapshotFlow { state.password.text }
            .onEach { password ->
                val passwordValidationState = userDataValidator.validatePassword(password.toString())
                state = state.copy(
                    passwordValidationState = passwordValidationState,
                    canRegister = state.isEmailValid && passwordValidationState.isValidPassword
                            && !state.isRegistering
                )
            }
            .launchIn(viewModelScope)
    }

    /*
    An action is considered as something being sent from the UI to the ViewModel
    (clicking a button, entering a TextField value, ecc...)
     */
    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            is RegisterAction.OnRegisterClick -> register()
            else -> Unit
        }
    }

    private fun register() {
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            val result = repository.register(
                state.email.text.toString().trim(),
                state.password.text.toString()
            )
            state = state.copy(isRegistering = false)

            when(result) {
                is Result.Failure -> {
                    if (result.failure == DataFailure.Network.CONFLICT) {
                        eventChannel.send(RegisterEvent.Failure(
                            UiText.StringResource(R.string.error_email_exists)
                        ))
                    } else {
                        eventChannel.send(RegisterEvent.Failure(result.failure.asUiText()))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
            }
        }
    }

}