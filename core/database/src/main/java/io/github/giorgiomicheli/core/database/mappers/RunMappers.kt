package io.github.giorgiomicheli.core.database.mappers

import io.github.giorgiomicheli.core.database.entity.RunEntity
import io.github.giorgiomicheli.core.domain.location.Location
import io.github.giorgiomicheli.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toDomain() = Run(
    id = id,
    duration = durationMillis.milliseconds,
    dateTimeUtc = Instant.parse(dateTimeUtc)
        .atZone(ZoneId.of("UTC")),
    distanceMeters = distanceMeters,
    location = Location(
        latitude = latitude,
        longitude = longitude
    ),
    maxSpeedKmh = maxSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    mapPictureUrl = mapPictureUrl
)

fun Run.toEntity(): RunEntity = RunEntity(
    id = id ?: ObjectId().toHexString(),
    durationMillis = duration.inWholeMilliseconds,
    maxSpeedKmh = maxSpeedKmh,
    dateTimeUtc = dateTimeUtc.toInstant().toString(),
    latitude = location.latitude,
    longitude = location.longitude,
    distanceMeters = distanceMeters,
    avgSpeedKmh = avgSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    mapPictureUrl = mapPictureUrl
)