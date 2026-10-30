package io.github.giorgiomicheli.run.domain

import io.github.giorgiomicheli.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {

    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { location1, location2 ->
                    location1.location.location.distanceTo(location2.location.location)
                }.sum().roundToInt()
            }
    }

    fun getMaxSpeed(locations: List<List<LocationTimestamp>>): Double {
        // takes the maximum of all the values produced by this block of code
        return locations.maxOf {

            it.zipWithNext { location1, location2 ->
                // distance between two location expressed in meters
                val distance = location1.location.location.distanceTo(
                    other = location2.location.location
                )
                val hoursDifference = (location2.durationTimestamp - location1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)
                // If the hoursDifference is not zero we calculate the km/h
                if (hoursDifference == 0.0) 0.0
                else (distance / 1000.0) / hoursDifference
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf {locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                val altitude1 = location1.location.altitude
                val altitude2 = location2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }

}