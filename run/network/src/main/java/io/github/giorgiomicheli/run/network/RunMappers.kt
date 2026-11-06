package io.github.giorgiomicheli.run.network

import io.github.giorgiomicheli.core.domain.location.Location
import io.github.giorgiomicheli.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run = Run(
    id = id,
    duration = durationMillis.milliseconds,
    dateTimeUtc = Instant.parse(dateTimeUtc)
        .atZone(ZoneId.of("UTC")),
    distanceMeters = distanceMeters,
    location = Location(lat, long),
    maxSpeedKmh = maxSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    mapPictureUrl = mapPictureUrl
)

fun Run.toCreateRunRequest(): CreateRunRequest = CreateRunRequest(
    id = id!!,
    durationMillis = duration.inWholeMilliseconds,
    distanceMeters = distanceMeters,
    lat = location.latitude,
    long = location.longitude,
    avgSpeedKmh = avgSpeedKmh,
    maxSpeedKmh = maxSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    epochMillis = dateTimeUtc.toEpochSecond() * 1000L
)