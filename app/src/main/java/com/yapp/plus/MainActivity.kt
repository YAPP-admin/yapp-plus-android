package com.yapp.plus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.notification.SettlementNotificationTarget
import com.yapp.plus.navigation.AppNavigationRoute
import com.yapp.plus.notification.PUSH_AFTERPARTY_ID
import com.yapp.plus.notification.PUSH_NAVIGATION_TARGET
import com.yapp.plus.notification.PUSH_POST_ID
import com.yapp.plus.notification.PUSH_SESSION_ID
import com.yapp.plus.notification.PUSH_SETTLEMENT_ID
import com.yapp.plus.notification.PUSH_SOURCE
import com.yapp.plus.notification.PushRegistration
import com.yapp.plus.notification.pushPostId
import com.yapp.plus.notification.pushSessionId
import com.yapp.plus.notification.pushSettlement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    internal lateinit var pushRegistration: PushRegistration

    private var pendingPushPostId by mutableStateOf<Long?>(null)
    private var pendingPushSessionId by mutableStateOf<Long?>(null)
    private var pendingPushSettlement by mutableStateOf<SettlementNotificationTarget?>(null)
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            pushRegistration.synchronize()
            if (shouldRequestLocationAfterNotification) {
                shouldRequestLocationAfterNotification = false
                isHomePermissionRequestPending = false
                requestLocationPermissionIfNeeded()
            }
        }
    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    private var isHomePermissionRequestPending = false
    private var isCheckingHomePermissions = false
    private var isLocationPermissionRequestPending = false
    private var shouldRequestLocationAfterNotification = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        pendingPushPostId = intent.pushPostId()
        pendingPushSessionId = intent.pushSessionId()
        pendingPushSettlement = intent.pushSettlement()
        val barStyle =
            SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
        setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                AppNavigationRoute(
                    onExit = ::finish,
                    onSessionReady = ::onSessionReady,
                    onHomeEntered = ::onHomeEntered,
                    pushPostId = pendingPushPostId,
                    onPushPostOpened = ::consumePushTarget,
                    pushSessionId = pendingPushSessionId,
                    onPushSessionOpened = ::consumePushTarget,
                    pushSettlement = pendingPushSettlement,
                    onPushSettlementOpened = ::consumePushTarget,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pushRegistration.synchronize()
        if (isLocationPermissionRequestPending) requestLocationPermissionIfNeeded()
        requestHomePermissionsIfPossible()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val postId = intent.pushPostId()
        val sessionId = intent.pushSessionId()
        val settlement = intent.pushSettlement()
        if (postId == null && sessionId == null && settlement == null) return
        setIntent(intent)
        pendingPushPostId = postId
        pendingPushSessionId = sessionId
        pendingPushSettlement = settlement
    }

    private fun onSessionReady() {
        pushRegistration.synchronize()
    }

    private fun onHomeEntered() {
        isHomePermissionRequestPending = true
        requestHomePermissionsIfPossible()
    }

    private fun requestHomePermissionsIfPossible() {
        if (!canRequestHomePermissions()) return
        isHomePermissionRequestPending = false
        isCheckingHomePermissions = true
        lifecycleScope.launch {
            try {
                requestHomePermissions()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                requestLocationPermissionIfNeeded()
                isHomePermissionRequestPending = false
            } finally {
                isCheckingHomePermissions = false
                pushRegistration.synchronize()
                if (isHomePermissionRequestPending) requestHomePermissionsIfPossible()
            }
        }
    }

    private fun canRequestHomePermissions(): Boolean =
        isHomePermissionRequestPending &&
            !isCheckingHomePermissions &&
            !shouldRequestLocationAfterNotification &&
            lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private suspend fun requestHomePermissions() {
        val shouldRequestNotification = pushRegistration.fetchEnabled()
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isHomePermissionRequestPending = true
            return
        }
        if (shouldRequestNotification) {
            shouldRequestLocationAfterNotification = true
            if (launchNotificationPermissionIfNeeded()) {
                isHomePermissionRequestPending = false
                return
            }
        }
        shouldRequestLocationAfterNotification = false
        requestLocationPermissionIfNeeded()
        isHomePermissionRequestPending = false
    }

    private fun launchNotificationPermissionIfNeeded(): Boolean {
        val preferences = getPreferences(MODE_PRIVATE)
        if (
            !shouldRequestNotificationPermission() ||
            preferences.getBoolean(NOTIFICATION_PERMISSION_REQUESTED, false)
        ) {
            return false
        }
        preferences.edit().putBoolean(NOTIFICATION_PERMISSION_REQUESTED, true).apply()
        return try {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            true
        } catch (_: IllegalStateException) {
            preferences.edit().remove(NOTIFICATION_PERMISSION_REQUESTED).apply()
            false
        }
    }

    private fun shouldRequestNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissionIfNeeded() {
        if (hasPreciseLocationPermission()) {
            isLocationPermissionRequestPending = false
            return
        }
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isLocationPermissionRequestPending = true
            return
        }
        launchLocationPermissionIfNeeded()
    }

    private fun hasPreciseLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    private fun launchLocationPermissionIfNeeded() {
        isLocationPermissionRequestPending = false
        val preferences = getPreferences(MODE_PRIVATE)
        if (preferences.getBoolean(LOCATION_PERMISSION_REQUESTED, false)) return
        preferences.edit().putBoolean(LOCATION_PERMISSION_REQUESTED, true).apply()
        try {
            locationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        } catch (_: IllegalStateException) {
            preferences.edit().remove(LOCATION_PERMISSION_REQUESTED).apply()
        }
    }

    private fun consumePushTarget() {
        pendingPushPostId = null
        pendingPushSessionId = null
        pendingPushSettlement = null
        intent.removeExtra(PUSH_SOURCE)
        intent.removeExtra(PUSH_POST_ID)
        intent.removeExtra(PUSH_SESSION_ID)
        intent.removeExtra(PUSH_NAVIGATION_TARGET)
        intent.removeExtra(PUSH_AFTERPARTY_ID)
        intent.removeExtra(PUSH_SETTLEMENT_ID)
    }

    private companion object {
        const val NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
        const val LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
    }
}
