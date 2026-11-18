package com.yapp.plus.data.session

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.math.roundToInt

class DeviceSessionDistanceDataSource(
    private val context: Context,
) : SessionDistanceDataSource {
    override suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance {
        val hasFineLocation = hasPermission(ACCESS_FINE_LOCATION)
        if (!hasFineLocation && !hasPermission(ACCESS_COARSE_LOCATION)) {
            return SessionDistance.PermissionRequired
        }
        val manager =
            context.getSystemService(LocationManager::class.java)
                ?: return SessionDistance.Unavailable
        return try {
            measureDistance(manager, coordinates, hasFineLocation)
        } catch (_: SecurityException) {
            SessionDistance.PermissionRequired
        } catch (_: IllegalArgumentException) {
            SessionDistance.Unavailable
        }
    }

    private suspend fun measureDistance(
        manager: LocationManager,
        target: SessionCoordinates,
        hasFineLocation: Boolean,
    ): SessionDistance {
        if (!LocationManagerCompat.isLocationEnabled(manager)) {
            return SessionDistance.LocationDisabled
        }
        val provider = manager.findProvider(hasFineLocation) ?: return SessionDistance.Unavailable
        val location =
            withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                manager.awaitLocation(provider)
            } ?: return SessionDistance.Unavailable
        return SessionDistance.Measured(
            meters = location.distanceTo(target),
            isApproximate = !hasFineLocation,
        )
    }

    private fun LocationManager.findProvider(hasFineLocation: Boolean): String? =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isProviderEnabled(FUSED_PROVIDER) ->
                FUSED_PROVIDER
            hasFineLocation && isProviderEnabled(GPS_PROVIDER) -> GPS_PROVIDER
            isProviderEnabled(NETWORK_PROVIDER) -> NETWORK_PROVIDER
            else -> null
        }

    // fetchDistanceTo checks both runtime grants and handles revocation during the request.
    @SuppressLint("MissingPermission")
    private suspend fun LocationManager.awaitLocation(provider: String): Location? =
        suspendCancellableCoroutine { continuation ->
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            LocationManagerCompat.getCurrentLocation(
                this,
                provider,
                signal,
                ContextCompat.getMainExecutor(context),
                continuation::resume,
            )
        }

    private fun Location.distanceTo(target: SessionCoordinates): Int {
        val result = FloatArray(1)
        Location.distanceBetween(latitude, longitude, target.latitude, target.longitude, result)
        return result[0].roundToInt()
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED

    private companion object {
        const val LOCATION_TIMEOUT_MS = 15_000L
    }
}
