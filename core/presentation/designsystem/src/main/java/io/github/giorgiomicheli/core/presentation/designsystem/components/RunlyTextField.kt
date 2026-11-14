package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.core.presentation.designsystem.CheckIcon
import io.github.giorgiomicheli.core.presentation.designsystem.EmailIcon
import io.github.giorgiomicheli.core.presentation.designsystem.Mint
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme

@Composable
fun RunlyTextField(
    modifier: Modifier,
    state: TextFieldState,
    startIcon: ImageVector?,
    endIcon: ImageVector?,
    hint: String,
    title: String?,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    additionalInfo: String? = null,
    endIconTint: Color = Mint // l'icona finale oggi è solo la spunta di validità
) {
    var isFocused by remember { mutableStateOf(false) }
    val isError = error != null

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RunlyFieldLabel(
            title = title,
            supportingText = error ?: additionalInfo,
            isError = isError
        )
        BasicTextField(
            state = state,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .runlyInputContainer(isFocused = isFocused, isError = isError)
                .onFocusChanged { isFocused = it.isFocused },
            decorator = { innerBox ->
                RunlyInputDecoration(
                    innerBox = innerBox,
                    isEmpty = state.text.isEmpty(),
                    hint = hint,
                    isFocused = isFocused,
                    startIcon = startIcon,
                    trailing = endIcon?.let { icon ->
                        { Icon(imageVector = icon, contentDescription = null, tint = endIconTint) }
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun RunlyTextFieldPreview() {
    RunlyTheme {
        RunlyTextField(
            state = rememberTextFieldState(),
            startIcon = EmailIcon,
            endIcon = CheckIcon,
            hint = "example@test.com",
            title = "Email",
            additionalInfo = "Must be a valid email",
            modifier = Modifier.fillMaxWidth()
        )
    }
}