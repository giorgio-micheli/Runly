package io.github.giorgiomicheli.core.presentation.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/*
    We will use only the dark theme in this app
 */
val DarkColorScheme = darkColorScheme(
    primary = Turf,
    onPrimary = Black,
    primaryContainer = TurfSoft,
    onPrimaryContainer = TurfText,
    background = Black,
    onBackground = Chalk,
    secondary = ClayText,
    onSecondary = Black,
    secondaryContainer = ClaySoft,
    onSecondaryContainer = ClayText,
    surface = BlackSurface,
    onSurface = Chalk,
    surfaceVariant = BlackSurface2,
    onSurfaceVariant = ChalkMuted,
    outline = BlackLineStrong,
    outlineVariant = BlackLine,
    error = Crimson,
    onError = Black,
    errorContainer = CrimsonSoft,
    onErrorContainer = Crimson,
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