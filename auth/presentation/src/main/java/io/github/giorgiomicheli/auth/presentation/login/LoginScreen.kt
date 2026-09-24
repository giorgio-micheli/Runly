package io.github.giorgiomicheli.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.giorgiomicheli.auth.presentation.register.RegisterAction
import io.github.giorgiomicheli.core.presentation.designsystem.EmailIcon
import io.github.giorgiomicheli.core.presentation.designsystem.Poppins
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyGray
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.components.GradientBackground
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyPasswordTextField
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyTextField
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.core.presentation.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onSignUpClick: () -> Unit,
    onLoginSuccessful: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                    ).show()
            }
            is LoginEvent.LoginSuccess -> {
                Toast.makeText(
                    context,
                    R.string.youre_logged_in,
                    Toast.LENGTH_LONG
                ).show()

                onLoginSuccessful()
            }
            is LoginEvent.RegisterButtonClicked -> onSignUpClick()
        }

    }

    LoginScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    GradientBackground {
        // We need to use BoxWithConstraints because we're usign both verticalScroll and the last
        // composable is using weight
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 32.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.hi_there),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(id = R.string.runly_welcome_text),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(48.dp))

                RunlyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.email,
                    startIcon = EmailIcon,
                    endIcon = null,
                    hint = stringResource(R.string.example_email),
                    title = stringResource(id = R.string.email),
                )
                Spacer(modifier = Modifier.height(16.dp))

                RunlyPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.password,
                    isPasswordVisible = state.isPasswordVisible,
                    onTogglePasswordVisibility = {
                        onAction(LoginAction.OnTogglePasswordVisibility)
                    },
                    hint = stringResource(R.string.password),
                    title = stringResource(R.string.password),
                )
                Spacer(modifier = Modifier.height(32.dp))

                RunlyActionButton(
                    text = stringResource(id = R.string.login),
                    isLoading = state.isLogginIn,
                    enabled = state.canLogin,
                    onClick = {
                        onAction(LoginAction.OnLoginClick)
                    }
                )

                val annotatedString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Poppins,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        append(stringResource(id = R.string.dont_have_an_account) + " ")

                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "clickable_text",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = Poppins
                                    )
                                ),
                                linkInteractionListener = {
                                    onAction(LoginAction.OnRegisterClick)
                                }
                            )
                        ) {
                            append(stringResource(id = R.string.sign_up))
                        }
                    }
                }

                Box(
                    modifier = Modifier.
                    fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(text = annotatedString)
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    RunlyTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}