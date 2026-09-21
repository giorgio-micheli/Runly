package io.github.giorgiomicheli.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    /*
    We use state flows only if we need reactivity.
    If we just need to expose the state to the UI then compose state is perfectly ok.
    Even when we use state flows in the end we turn them into compose state.
     */
    var state by mutableStateOf(RegisterState())
        private set

    fun onAction(action: RegisterAction) {

    }

}