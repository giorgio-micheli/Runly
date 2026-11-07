package io.github.giorgiomicheli.run.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.giorgiomicheli.core.domain.location.LocationWithAltitude
import io.github.giorgiomicheli.run.domain.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.seconds

/**
 * This class encapsulate the complexity of the Google Play Services location api and expose it
 * as a simple Kotlin Flow.
 */
class AndroidLocationObserver(
    private val context: Context
) : LocationObserver {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> {
        /*
            Creates a flow from a callback based api, it internally uses a Channel to do that.
         */
        return callbackFlow {
            val locationManager = context.getSystemService<LocationManager>()!!
            var isGpsEnabled = false
            var isNetworkEnabled = false

            // While the device has not both the gps and network enabled we wait
            while(!isGpsEnabled && !isNetworkEnabled) {
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                // We wait 3 seconds only if neither the gps and the network are enabled
                if (!isGpsEnabled && !isNetworkEnabled) {
                    delay(3.seconds)
                }
            }

            // Check if the location permissions on the device are granted
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close()
            } else {

                // Retrieves the cached last known position, it's used as an initial value for the flow
                client.lastLocation.addOnSuccessListener {
                    it?.let {location ->
                        trySend(location.toLocationWithAltitude())
                    }
                }

                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { location ->
                            trySend(location.toLocationWithAltitude())
                        }
                    }
                }

                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
                // Unregister the callback when nobody is no more collecting the flow
                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    override fun observeGeolocalizationEnabled(): Flow<Boolean> {
        return callbackFlow {
            val locationManager = context.getSystemService<LocationManager>()!!

            fun currentValue() = LocationManagerCompat.isLocationEnabled(locationManager)

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    trySend(currentValue())
                }
            }

            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            // We send an initial value cause the broadcast receiver only fires when the user
            // taps on the geolocalization quick setting. Otherwise we would not be able to know
            // if the geolocalization is active until the user's tap.
            trySend(currentValue())

            awaitClose {
                context.unregisterReceiver(receiver)
            }
            // returns a new cold flow that behaves like a hot flow, where if the new value that
            // arrives is the same as the current one, it doesn't emit that value.
        }.distinctUntilChanged()
    }
}