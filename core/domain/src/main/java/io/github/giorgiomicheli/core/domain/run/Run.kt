package io.github.giorgiomicheli.core.domain.run

import io.github.giorgiomicheli.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: String?, // null if it's a new run not synched with the back-end yet.
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    // We'll push the screenshot of the run to our back-end, which will then give us a picture url
    // hosted on aws s3
    val mapPictureUrl: String?
) {
    val avgSpeedKmh: Double
        get() = (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
}