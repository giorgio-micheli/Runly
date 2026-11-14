package io.github.giorgiomicheli.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import io.github.giorgiomicheli.auth.presentation.components.AuthLayout
import io.github.giorgiomicheli.core.presentation.designsystem.MailIcon
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyPasswordTextField
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyTextField
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyTextWithLink
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.core.presentation.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onSignUpClick: () -> Unit,
    onLoginSuccessful: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_LONG).show()
            }
            is LoginEvent.LoginSuccess -> {
                Toast.makeText(context, R.string.youre_logged_in, Toast.LENGTH_LONG).show()
                onLoginSuccessful()
            }
            is LoginEvent.RegisterButtonClicked -> onSignUpClick()
        }
    }

    LoginScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                LoginAction.OnBackClick -> onBackClick()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    AuthLayout(
        title = stringResource(id = R.string.login_title),
        subtitle = stringResource(id = R.string.login_subtitle),
        onBackClick = { onAction(LoginAction.OnBackClick) },
        footer = {
            RunlyTextWithLink(
                text = stringResource(id = R.string.new_to_runly),
                linkText = stringResource(id = R.string.create_account),
                onLinkClick = { onAction(LoginAction.OnRegisterClick) }
            )
        }
    ) {
        RunlyTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.email,
            startIcon = MailIcon,
            endIcon = null,
            keyboardType = KeyboardType.Email,
            hint = stringResource(id = R.string.example_email),
            title = stringResource(id = R.string.email)
        )
        RunlyPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.password,
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { onAction(LoginAction.OnTogglePasswordVisibility) },
            hint = stringResource(id = R.string.password),
            title = stringResource(id = R.string.password)
        )
        RunlyActionButton(
            text = stringResource(id = R.string.sign_in),
            isLoading = state.isLogginIn,
            enabled = state.canLogin && !state.isLogginIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.space4),
            onClick = { onAction(LoginAction.OnLoginClick) }
        )
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    RunlyTheme {
        LoginScreen(state = LoginState(), onAction = {})
    }
}