package io.github.giorgiomicheli.core.presentation.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    primary = RunlyGreen,
    background = RunlyBlack,
    surface = RunlyDarkGray,
    secondary = RunlyWhite,
    tertiary = RunlyWhite,
    primaryContainer = RunlyGreen30,
    onPrimary = RunlyBlack,
    onBackground = RunlyWhite,
    onSurface = RunlyWhite,
    onSurfaceVariant = RunlyGray
)

@Composable
fun RunlyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = typography,
        content = content
    )
}