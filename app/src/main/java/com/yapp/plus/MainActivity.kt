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
import androidx.lifecycle.lifecycleScope
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.navigation.AppNavigationRoute
import com.yapp.plus.notification.PUSH_POST_ID
import com.yapp.plus.notification.PUSH_SOURCE
import com.yapp.plus.notification.PushRegistration
import com.yapp.plus.notification.pushPostId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    internal lateinit var pushRegistration: PushRegistration

    private var pendingPushPostId by mutableStateOf<Long?>(null)
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            pushRegistration.synchronize()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        pendingPushPostId = intent.pushPostId()
        val barStyle =
            SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
        setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                AppNavigationRoute(
                    onExit = ::finish,
                    onSessionReady = ::onSessionReady,
                    pushPostId = pendingPushPostId,
                    onPushPostOpened = ::consumePushPost,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pushRegistration.synchronize()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val postId = intent.pushPostId() ?: return
        setIntent(intent)
        pendingPushPostId = postId
    }

    private fun onSessionReady() {
        if (!pushRegistration.isConfigured()) return
        lifecycleScope.launch {
            try {
                if (pushRegistration.fetchEnabled()) requestNotificationPermission()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Settings offers a retry if the saved choice cannot be read.
            } finally {
                pushRegistration.synchronize()
            }
        }
    }

    private fun requestNotificationPermission() {
        val preferences = getPreferences(MODE_PRIVATE)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED &&
            !preferences.getBoolean(NOTIFICATION_PERMISSION_REQUESTED, false)
        ) {
            preferences.edit().putBoolean(NOTIFICATION_PERMISSION_REQUESTED, true).apply()
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun consumePushPost() {
        pendingPushPostId = null
        intent.removeExtra(PUSH_SOURCE)
        intent.removeExtra(PUSH_POST_ID)
    }

    private companion object {
        const val NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
