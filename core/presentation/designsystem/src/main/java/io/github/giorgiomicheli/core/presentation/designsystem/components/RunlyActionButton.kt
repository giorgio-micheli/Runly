package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.core.presentation.designsystem.Radius
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.Sizes
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing

private val ButtonShape = RoundedCornerShape(Radius.md)
private val ButtonPadding = PaddingValues(horizontal = Spacing.space5)

@Composable
fun RunlyActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        modifier = modifier.height(Sizes.controlHeight)
    ) {
        RunlyButtonContent(text = text, isLoading = isLoading)
    }
}

@Composable
fun RunlyOutlinedActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(width = 1.5.dp, color = MaterialTheme.colorScheme.outline),
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        modifier = modifier.height(Sizes.controlHeight)
    ) {
        RunlyButtonContent(text = text, isLoading = isLoading)
    }
}

/** Testo e spinner sovrapposti: l'alpha li scambia senza togliere nodi dall'albero. */
@Composable
private fun RunlyButtonContent(
    text: String,
    isLoading: Boolean
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(20.dp)
                .alpha(if (isLoading) 1f else 0f),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.alpha(if (isLoading) 0f else 1f)
        )
    }
}

@Preview
@Composable
private fun RunlyActionButtonPreview() {
    RunlyTheme {
        RunlyActionButton(text = "Sign in", isLoading = false, onClick = {})
    }
}

@Preview
@Composable
private fun RunlyOutlinedActionButtonPreview() {
    RunlyTheme {
        RunlyOutlinedActionButton(text = "Sign in", isLoading = false, onClick = {})
    }
}