package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.giorgiomicheli.core.presentation.designsystem.RunIcon
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme

@Composable
fun RunlyFloatingActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    isRunning: Boolean,
    contentDescription: String? = null,
    iconSize: Dp = 25.dp
) {
    val primary = MaterialTheme.colorScheme.primary

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isRunning) {
        if (!isRunning) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            progress.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .drawBehind {
                if (!isRunning) {
                    // 'progress' is read only here, only the draw phase changes.
                    drawPulse(progress.value, phaseOffset = 0f, baseAlpha = 0.4f, color = primary)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private fun DrawScope.drawPulse(
    progress: Float,
    phaseOffset: Float,
    baseAlpha: Float,
    color: Color
) {
    val phase = (progress + phaseOffset) % 1f
    val eased = FastOutSlowInEasing.transform(phase)
    drawCircle(
        color = color,
        radius = lerp(25.dp.toPx(), 37.5.dp.toPx(), eased), // 50.dp -> 75.dp di diametro
        alpha = baseAlpha * (1f - phase)
    )
}

@Preview
@Composable
private fun RunlyFloatingActionButtonPreview() {
    RunlyTheme {
        RunlyFloatingActionButton(
            icon = RunIcon,
            onClick = {},
            isRunning = false
        )
    }
}