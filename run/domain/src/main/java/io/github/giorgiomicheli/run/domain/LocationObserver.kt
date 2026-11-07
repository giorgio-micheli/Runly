package io.github.giorgiomicheli.run.domain

import io.github.giorgiomicheli.core.domain.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    fun observeLocation(interval: Long): Flow<LocationWithAltitude>
    /**
     * Emits whether the device location (GPS / network) is currently enabled.
     * Emits the current value right away and then every change.
     */
    fun observeGeolocalizationEnabled(): Flow<Boolean>
}