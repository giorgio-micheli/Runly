package io.github.giorgiomicheli.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import io.github.giorgiomicheli.core.domain.location.Location

/*
    A polyline will be divided in different parts, based on how fast the user is running.
    Each part will have a color to distinguish between a fast runned trait compared to slow one.
 */
data class PolylineUi(
    val location1: Location,
    val location2: Location,
    val color: Color
)
