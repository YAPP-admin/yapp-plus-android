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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.feature.main.home.components.HomeSessionDistance
import com.yapp.plus.feature.main.home.model.SessionDistanceUiState

@Composable
internal fun HomeDistanceRoute(
    coordinates: SessionCoordinates,
    viewModel: HomeDistanceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isRequestingPermission by rememberSaveable { mutableStateOf(false) }
    val permissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isRequestingPermission = false
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                return@rememberLauncherForActivityResult
            }
            if (it.values.any { granted -> granted } || context.hasLocationPermission()) {
                viewModel.measure(coordinates)
            } else {
                viewModel.onPermissionDenied()
            }
        }
    val failure = (state as? SessionDistanceUiState.Result)?.distance
    DisposableEffect(viewModel) { onDispose { viewModel.clear() } }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.cancelMeasurement() }

    HomeSessionDistance(
        state = if (isRequestingPermission) SessionDistanceUiState.Loading else state,
        onMeasure = {
            if (context.hasLocationPermission()) {
                viewModel.measure(coordinates)
            } else if (!isRequestingPermission) {
                isRequestingPermission = true
                permissions.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
            }
        },
        onOpenSettings = {
            val intent =
                if (failure == SessionDistance.LocationDisabled) {
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData("package:${context.packageName}".toUri())
                }
            context.startActivity(intent)
        },
    )
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
