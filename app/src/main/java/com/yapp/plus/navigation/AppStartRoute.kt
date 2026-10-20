package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yapp.plus.feature.login.model.GoogleLoginCredential
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val SPLASH_DURATION_MILLIS = 1_000L

@Composable
internal fun AppStartRoute(
    onExit: () -> Unit,
    startDestination: AuthDemoDestination,
    modifier: Modifier = Modifier,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit = {},
) {
    var isSplashVisible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS.milliseconds)
        isSplashVisible = false
    }

    if (isSplashVisible) {
        YappSplashScreen(modifier = modifier)
    } else {
        AuthDemoRoute(
            onExit = onExit,
            startDestination = startDestination,
            modifier = modifier,
            onGoogleCredentialReceived = onGoogleCredentialReceived,
        )
    }
}
