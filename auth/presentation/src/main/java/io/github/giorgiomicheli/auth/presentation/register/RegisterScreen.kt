package io.github.giorgiomicheli.auth.presentation.register

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.giorgiomicheli.auth.domain.PasswordValidationState
import io.github.giorgiomicheli.auth.domain.UserDataValidator
import io.github.giorgiomicheli.auth.presentation.components.AuthLayout
import io.github.giorgiomicheli.core.presentation.designsystem.CheckIcon
import io.github.giorgiomicheli.core.presentation.designsystem.CloseIcon
import io.github.giorgiomicheli.core.presentation.designsystem.MailIcon
import io.github.giorgiomicheli.core.presentation.designsystem.Mint
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.Sizes
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyPasswordTextField
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyTextField
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyTextWithLink
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.core.presentation.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onSignInClick: () -> Unit,
    onSuccessfulRegistration: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.Failure -> {
                keyboardController?.hide()
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_LONG).show()
            }
            is RegisterEvent.RegistrationSuccess -> {
                keyboardController?.hide()
                Toast.makeText(context, R.string.registration_successful, Toast.LENGTH_LONG).show()
                onSuccessfulRegistration()
            }
            is RegisterEvent.LoginButtonClicked -> onSignInClick()
        }
    }

    RegisterScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RegisterAction.OnBackClick -> onBackClick()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    AuthLayout(
        title = stringResource(id = R.string.register_title),
        subtitle = stringResource(id = R.string.register_subtitle),
        onBackClick = { onAction(RegisterAction.OnBackClick) },
        footer = {
            RunlyTextWithLink(
                text = stringResource(id = R.string.already_have_an_account),
                linkText = stringResource(id = R.string.sign_in),
                onLinkClick = { onAction(RegisterAction.OnLoginClick) }
            )
        }
    ) {
        RunlyTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.email,
            startIcon = MailIcon,
            endIcon = if (state.isEmailValid) CheckIcon else null,
            hint = stringResource(id = R.string.example_email),
            title = stringResource(id = R.string.email),
            additionalInfo = stringResource(id = R.string.must_be_a_valid_email),
            keyboardType = KeyboardType.Email
        )
        RunlyPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.password,
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { onAction(RegisterAction.OnTogglePasswordVisibilityClick) },
            hint = stringResource(id = R.string.password),
            title = stringResource(id = R.string.password)
        )
        PasswordRequirements(validation = state.passwordValidationState)
        RunlyActionButton(
            text = stringResource(id = R.string.create_account),
            isLoading = state.isRegistering,
            enabled = state.canRegister && !state.isRegistering,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.space4),
            onClick = { onAction(RegisterAction.OnRegisterClick) }
        )
    }
}

/** Griglia 2×2 dei requisiti della password. */
@Composable
private fun PasswordRequirements(
    validation: PasswordValidationState,
    modifier: Modifier = Modifier
) {
    val requirements = listOf(
        stringResource(id = R.string.password_min_length, UserDataValidator.MIN_PASSWORD_LENGTH) to validation.hasMinLength,
        stringResource(id = R.string.password_one_number) to validation.hasNumber,
        stringResource(id = R.string.password_lowercase) to validation.hasLowerCaseCharacter,
        stringResource(id = R.string.password_uppercase) to validation.hasUpperCaseCharacter
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.space2)
    ) {
        requirements.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.space3)
            ) {
                row.forEach { (text, isValid) ->
                    PasswordRequirement(
                        text = text,
                        isValid = isValid,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.space2)
    ) {
        Icon(
            imageVector = if (isValid) CheckIcon else CloseIcon,
            contentDescription = null,
            tint = if (isValid) Mint else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(Sizes.iconSm)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
            color = if (isValid) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RegisterScreenPreview() {
    RunlyTheme {
        RegisterScreen(state = RegisterState(), onAction = {})
    }
}