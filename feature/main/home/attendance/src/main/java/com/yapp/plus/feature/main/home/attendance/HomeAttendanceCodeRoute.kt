package com.yapp.plus.feature.main.home.attendance

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yapp.plus.core.ui.extension.hasPermission
import com.yapp.plus.core.ui.extension.openAttendanceLocationSettings
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.domain.session.SessionCoordinates

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
