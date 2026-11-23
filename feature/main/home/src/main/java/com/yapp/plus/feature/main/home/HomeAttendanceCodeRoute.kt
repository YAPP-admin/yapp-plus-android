package com.yapp.plus.feature.main.home

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
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeAttendanceLocationIssue
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSubmissionStage

@Composable
internal fun HomeAttendanceCodeRoute(
    session: CurrentWeekSessionUiModel?,
    state: HomeInteractionUiState,
    viewModel: HomeInteractionViewModel,
    onRefreshSession: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(session?.sessionId, session?.coordinates, session?.attendanceStatus) {
        viewModel.cancelAttendanceLocation()
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                viewModel.onAttendancePermissionResult(
                    session = session,
                    hasPrecisePermission = context.hasPermission(ACCESS_FINE_LOCATION),
                    hasApproximatePermission = context.hasPermission(ACCESS_COARSE_LOCATION),
                )
            } else {
                viewModel.cancelAttendanceLocation()
            }
        }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.cancelAttendanceLocation() }
    DisposableEffect(viewModel) { onDispose { viewModel.cancelAttendanceLocation() } }

    HomeSheetContent(
        state = state,
        onCodeChange = viewModel::updateCode,
        onConfirm = {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                viewModel.confirmAttendance(session, context.hasPermission(ACCESS_FINE_LOCATION))
            ) {
                runCatching {
                    permissionLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
                }.onFailure {
                    viewModel.onAttendancePermissionResult(
                        session = session,
                        hasPrecisePermission = false,
                        hasApproximatePermission = context.hasPermission(ACCESS_COARSE_LOCATION),
                    )
                }
            }
        },
        isCheckingLocation =
            state.submissionStage == HomeSubmissionStage.RequestingLocationPermission ||
                state.submissionStage == HomeSubmissionStage.CheckingLocation,
        locationIssue = state.locationIssue,
        onLocationRecovery = { issue ->
            if (issue == HomeAttendanceLocationIssue.SessionUnavailable) {
                onRefreshSession()
            } else if (!context.openAttendanceLocationSettings(issue)) {
                viewModel.onLocationSettingsUnavailable()
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
