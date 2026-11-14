package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.giorgiomicheli.core.presentation.designsystem.Radius
import io.github.giorgiomicheli.core.presentation.designsystem.Sizes
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing

/** Riga sopra il campo: etichetta a sinistra, testo di supporto (o errore) a destra. */
@Composable
internal fun RunlyFieldLabel(
    title: String?,
    supportingText: String?,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    if (title == null && supportingText == null) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Spacer(modifier = Modifier)
        }
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Il "pozzetto" del campo: 56dp, fondo surface-2, bordo 2dp verde quando ha il focus. */
@Composable
internal fun Modifier.runlyInputContainer(
    isFocused: Boolean,
    isError: Boolean
): Modifier {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(Radius.md)

    val background by animateColorAsState(
        targetValue = if (isFocused) colors.surface else colors.surfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "inputBackground"
    )
    val border by animateColorAsState(
        targetValue = when {
            isError -> colors.error
            isFocused -> colors.onPrimaryContainer // brand-text
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "inputBorder"
    )

    return this
        .height(Sizes.fieldHeight)
        .clip(shape)
        .background(background)
        .border(width = 2.dp, color = border, shape = shape)
        .padding(horizontal = Spacing.space4)
}

/** Contenuto del campo: icona iniziale, testo o placeholder, elemento finale opzionale. */
@Composable
internal fun RunlyInputDecoration(
    innerBox: @Composable () -> Unit,
    isEmpty: Boolean,
    hint: String,
    isFocused: Boolean,
    startIcon: ImageVector?,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.space3)
    ) {
        if (startIcon != null) {
            Icon(
                imageVector = startIcon,
                contentDescription = null,
                tint = if (isFocused) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isEmpty) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            innerBox()
        }
        trailing?.invoke()
    }
}