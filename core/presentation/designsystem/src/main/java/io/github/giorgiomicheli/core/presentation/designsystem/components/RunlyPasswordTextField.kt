package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.core.presentation.designsystem.EyeIcon
import io.github.giorgiomicheli.core.presentation.designsystem.EyeOffIcon
import io.github.giorgiomicheli.core.presentation.designsystem.LockIcon
import io.github.giorgiomicheli.core.presentation.designsystem.R
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing

@Composable
fun RunlyPasswordTextField(
    modifier: Modifier,
    state: TextFieldState,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    hint: String,
    title: String?,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RunlyFieldLabel(title = title, supportingText = null, isError = false)
        BasicSecureTextField(
            state = state,
            textObfuscationMode = if (isPasswordVisible) TextObfuscationMode.Visible
            else TextObfuscationMode.Hidden,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .runlyInputContainer(isFocused = isFocused, isError = false)
                .onFocusChanged { isFocused = it.isFocused },
            decorator = { innerBox ->
                RunlyInputDecoration(
                    innerBox = innerBox,
                    isEmpty = state.text.isEmpty(),
                    hint = hint,
                    isFocused = isFocused,
                    startIcon = LockIcon,
                    trailing = {
                        IconButton(
                            onClick = onTogglePasswordVisibility,
                            // l'area di tocco resta 48dp, l'icona si allinea al bordo interno
                            modifier = Modifier.offset(x = Spacing.space3)
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) EyeOffIcon else EyeIcon,
                                contentDescription = if (isPasswordVisible) {
                                    stringResource(id = R.string.hide_password)
                                } else stringResource(id = R.string.show_password),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun RunlyPasswordTextFieldPreview() {
    RunlyTheme {
        RunlyPasswordTextField(
            state = rememberTextFieldState(),
            hint = "Password",
            title = "Password",
            modifier = Modifier.fillMaxWidth(),
            isPasswordVisible = false,
            onTogglePasswordVisibility = {}
        )
    }
}