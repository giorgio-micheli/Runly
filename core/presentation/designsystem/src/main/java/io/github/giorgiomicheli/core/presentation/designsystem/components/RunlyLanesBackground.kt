package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme

@Composable
fun RunlyLanesBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val laneColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                // Corsia esterna (curva larga) e interna (curva stretta), come nel logo
                drawLane(laneColor, fromRight = 218.dp, fromBottom = 120.dp, radius = 132.dp)
                drawLane(laneColor, fromRight = 104.dp, fromBottom = 102.dp, radius = 36.dp)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            content = content
        )
    }
}

/**
 * Una corsia: sale dal fondo, curva di 90° verso destra ed esce dal bordo.
 * fromRight/fromBottom = punto in cui inizia la curva, misurato dall'angolo in basso a destra.
 */
private fun DrawScope.drawLane(
    color: Color,
    fromRight: Dp,
    fromBottom: Dp,
    radius: Dp,
    width: Dp = 60.dp
) {
    val x = size.width - fromRight.toPx()
    val curveStartY = size.height - fromBottom.toPx()
    val r = radius.toPx()
    val overflow = width.toPx() // fa partire e finire la corsia fuori schermo

    val path = Path().apply {
        moveTo(x, size.height + overflow)
        lineTo(x, curveStartY)
        arcTo(
            rect = Rect(left = x, top = curveStartY - r, right = x + 2 * r, bottom = curveStartY + r),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width + overflow, curveStartY - r)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = width.toPx(), cap = StrokeCap.Round)
    )
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RunlyLanesBackgroundPreview() {
    RunlyTheme {
        RunlyLanesBackground { }
    }
}