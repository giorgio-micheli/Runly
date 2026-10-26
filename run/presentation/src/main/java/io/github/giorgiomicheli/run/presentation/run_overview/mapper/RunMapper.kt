package io.github.giorgiomicheli.run.presentation.run_overview.mapper

import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.presentation.ui.formatted
import io.github.giorgiomicheli.core.presentation.ui.toFormattedKM
import io.github.giorgiomicheli.core.presentation.ui.toFormattedKmh
import io.github.giorgiomicheli.core.presentation.ui.toFormattedMeters
import io.github.giorgiomicheli.core.presentation.ui.toFormattedPace
import io.github.giorgiomicheli.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())

    val formattedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    // We are sure that an id is present
    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKM(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}