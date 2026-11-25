package com.yapp.plus.feature.main.home.attendance

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.feature.main.home.attendance.model.HomeAttendanceLocationIssue

@Composable
fun HomeAttendanceCodeRoute(
    sessionId: Long?,
    coordinates: SessionCoordinates?,
    isAvailable: Boolean,
    isCheckingLocation: Boolean,
    locationIssue: HomeAttendanceLocationIssue?,
    sheetContent: @Composable (
        isCheckingLocation: Boolean,
        locationIssue: HomeAttendanceLocationIssue?,
        onConfirm: () -> Unit,
        onLocationRecovery: (HomeAttendanceLocationIssue) -> Unit,
    ) -> Unit,
    onCancelLocation: () -> Unit,
    onConfirmAttendance: (hasPrecisePermission: Boolean) -> Boolean,
    onPermissionResult: (hasPrecisePermission: Boolean, hasApproximatePermission: Boolean) -> Unit,
    onLocationSettingsUnavailable: () -> Unit,
    onRefreshSession: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(sessionId, coordinates, isAvailable) {
        onCancelLocation()
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                onPermissionResult(
                    context.hasPermission(ACCESS_FINE_LOCATION),
                    context.hasPermission(ACCESS_COARSE_LOCATION),
                )
            } else {
                onCancelLocation()
            }
        }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { onCancelLocation() }
    DisposableEffect(Unit) { onDispose { onCancelLocation() } }

    sheetContent(
        isCheckingLocation,
        locationIssue,
        {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                onConfirmAttendance(context.hasPermission(ACCESS_FINE_LOCATION))
            ) {
                runCatching {
                    permissionLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
                }.onFailure {
                    onPermissionResult(
                        false,
                        context.hasPermission(ACCESS_COARSE_LOCATION),
                    )
                }
            }
        },
        { issue ->
            if (issue == HomeAttendanceLocationIssue.SessionUnavailable) {
                onRefreshSession()
            } else if (!context.openAttendanceLocationSettings(issue)) {
                onLocationSettingsUnavailable()
            }
        },
    )
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

private fun Context.openAttendanceLocationSettings(issue: HomeAttendanceLocationIssue): Boolean {
    val intent =
        when (issue) {
            HomeAttendanceLocationIssue.LocationDisabled ->
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            HomeAttendanceLocationIssue.PermissionDenied,
            HomeAttendanceLocationIssue.PrecisePermissionRequired,
            ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData("package:$packageName".toUri())
            else -> return false
        }
    return runCatching { startActivity(intent) }.isSuccess
}
