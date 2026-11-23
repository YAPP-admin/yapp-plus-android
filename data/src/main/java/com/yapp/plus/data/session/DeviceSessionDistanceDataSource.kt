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
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import com.yapp.plus.domain.attendance.AttendanceLocationFailure
import com.yapp.plus.domain.attendance.AttendanceLocationReading
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

    override suspend fun fetchPreciseDistanceTo(coordinates: SessionCoordinates): AttendanceLocationReading {
        if (!coordinates.isValid) {
            return AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
        }
        if (!hasPermission(ACCESS_FINE_LOCATION)) {
            return AttendanceLocationReading.Failed(AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED)
        }
        val manager =
            context.getSystemService(LocationManager::class.java)
                ?: return AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
        return try {
            measurePreciseDistance(manager, coordinates)
        } catch (_: SecurityException) {
            AttendanceLocationReading.Failed(AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED)
        } catch (_: IllegalArgumentException) {
            AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
        }
    }

    private suspend fun measurePreciseDistance(
        manager: LocationManager,
        target: SessionCoordinates,
    ): AttendanceLocationReading {
        if (!LocationManagerCompat.isLocationEnabled(manager)) {
            return AttendanceLocationReading.Failed(AttendanceLocationFailure.LOCATION_DISABLED)
        }
        val provider =
            manager.findProvider(hasFineLocation = true)
                ?: return AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
        val location = withTimeoutOrNull(LOCATION_TIMEOUT_MS) { manager.awaitFreshLocation(provider) }
        return when {
            !hasPermission(ACCESS_FINE_LOCATION) ->
                AttendanceLocationReading.Failed(AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED)
            !LocationManagerCompat.isLocationEnabled(manager) ->
                AttendanceLocationReading.Failed(AttendanceLocationFailure.LOCATION_DISABLED)
            location == null -> AttendanceLocationReading.Failed(AttendanceLocationFailure.TIMED_OUT)
            else -> location.toAttendanceReading(target)
        }
    }

    private fun Location.toAttendanceReading(target: SessionCoordinates): AttendanceLocationReading {
        if (!SessionCoordinates(latitude, longitude).isValid) {
            return AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
        }
        return AttendanceLocationReading.Measured(
            meters = distanceMetersTo(target),
            accuracyMeters = accuracy.takeIf { hasAccuracy() },
        )
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
            meters = location.distanceMetersTo(target).roundToInt(),
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

    private fun Location.distanceMetersTo(target: SessionCoordinates): Float {
        val result = FloatArray(1)
        Location.distanceBetween(latitude, longitude, target.latitude, target.longitude, result)
        return result[0]
    }

    // A cached sample from before this attempt cannot authorize a new submission.
    @SuppressLint("MissingPermission")
    private suspend fun LocationManager.awaitFreshLocation(provider: String): Location =
        suspendCancellableCoroutine { continuation ->
            val requestedAt = SystemClock.elapsedRealtimeNanos()
            val listener =
                object : LocationListenerCompat {
                    override fun onLocationChanged(location: Location) {
                        val age = SystemClock.elapsedRealtimeNanos() - location.elapsedRealtimeNanos
                        if (location.elapsedRealtimeNanos < requestedAt) return
                        if (age !in 0..MAX_LOCATION_AGE_NANOS || !continuation.isActive) return
                        LocationManagerCompat.removeUpdates(this@awaitFreshLocation, this)
                        continuation.resume(location)
                    }
                }
            val request =
                LocationRequestCompat
                    .Builder(0)
                    .setQuality(LocationRequestCompat.QUALITY_HIGH_ACCURACY)
                    .build()
            LocationManagerCompat.requestLocationUpdates(
                this,
                provider,
                request,
                ContextCompat.getMainExecutor(context),
                listener,
            )
            continuation.invokeOnCancellation {
                LocationManagerCompat.removeUpdates(this, listener)
            }
        }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED

    private companion object {
        const val LOCATION_TIMEOUT_MS = 15_000L
        const val MAX_LOCATION_AGE_NANOS = 5_000_000_000L
    }
}
