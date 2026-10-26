package io.github.giorgiomicheli.run.location

import android.location.Location
import io.github.giorgiomicheli.core.domain.location.LocationWithAltitude

fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = io.github.giorgiomicheli.core.domain.location.Location(
            latitude = latitude,
            longitude = longitude
        ),
        altitude = altitude
    )
}