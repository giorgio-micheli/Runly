package io.github.giorgiomicheli.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import io.github.giorgiomicheli.core.domain.location.LocationTimestamp
import kotlin.math.abs
import kotlin.time.DurationUnit

object PolylineColorCalculator {

    fun locationsToColor(location1: LocationTimestamp, location2: LocationTimestamp): Color {
        val distanceMeters = location1.location.location.distanceTo(location2.location.location)
        val seconds = (location2.durationTimestamp - location1.durationTimestamp)
            .absoluteValue
            .toDouble(DurationUnit.SECONDS) // 0.407s remains 0.407s it doesn't become 0 as before when it was used inWholeSeconds()

        /*
        Handles the case where two points have the same timestamps. The velocity can't be calculated.
        We choose a fixed color for this case.
         */
        if (seconds <= 0.0) return Color.Green

        val speedKmh = (distanceMeters / seconds) * 3.6

        return interpolateColor(
            speedKmh = speedKmh,
            minSpeed = 5.0,
            maxSpeed = 20.0,
            colorStart = Color.Green,
            colorMid = Color.Yellow,
            colorEnd = Color.Red
        )

    }

    private fun interpolateColor(
        speedKmh: Double,
        minSpeed: Double,
        maxSpeed: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color
    ): Color {
        val ratio = ((speedKmh - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0 .. 1.0)
        val colorInt = if (ratio <= 0.5) {
            val midRatio = ratio / 0.5
            ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
        } else {
            val midToEndRatio = (ratio - 0.5) / 0.5
            ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())
        }

        return Color(colorInt)
    }
}