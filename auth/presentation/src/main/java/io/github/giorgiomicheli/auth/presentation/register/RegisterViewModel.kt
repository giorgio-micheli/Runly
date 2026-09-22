package io.github.giorgiomicheli.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.giorgiomicheli.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterViewModel(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    /*
    We use state flows only if we need reactivity.
    If we just need to expose the state to the UI then compose state is perfectly ok.
    Even when we use state flows in the end we turn them into compose state.
     */
    var state by mutableStateOf(RegisterState())
        private set

    init {
        snapshotFlow { state.email.text }
            .onEach { email ->
                state = state.copy(
                    isEmailValid = userDataValidator.isValidEmail(email.toString())
                )
            }
            .launchIn(viewModelScope)

        snapshotFlow { state.password.text }
            .onEach { password ->
                state = state.copy(
                    passwordValidationState = userDataValidator.validatePassword(password.toString())
                )
            }
            .launchIn(viewModelScope)
    }
    fun onAction(action: RegisterAction) {

    }

}