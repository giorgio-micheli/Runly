package io.github.giorgiomicheli.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import io.github.giorgiomicheli.core.domain.location.LocationTimestamp

@Composable
fun RunlyPolylines(locations: List<List<LocationTimestamp>>) {
    /*
    TODO: instead of creating a polyline for every couple of points in the map, we can have a
        single polyline and use the parameter "spans" in the official Polyline composable, to
        color a specific segment of a polyline. This way we'll have only one polyline instead of
        thousands per single run.
     */
    val polylines = remember(locations) {
        locations.map {
            it.zipWithNext { timestamp1, timestamp2 ->
                PolylineUi(
                    location1 = timestamp1.location.location,
                    location2 = timestamp2.location.location,
                    color = PolylineColorCalculator.locationsToColor(
                        location1 = timestamp1,
                        location2 = timestamp2
                    )
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { polylineUi ->
            Polyline(
                points = listOf(
                    LatLng(polylineUi.location1.latitude, polylineUi.location1.longitude),
                    LatLng(polylineUi.location2.latitude, polylineUi.location2.longitude),
                ),
                color = polylineUi.color,
                jointType = JointType.BEVEL
            )
        }
    }
}