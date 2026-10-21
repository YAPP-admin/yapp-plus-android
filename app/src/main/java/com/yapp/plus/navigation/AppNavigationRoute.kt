package com.yapp.plus.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.domain.auth.GoogleLoginCredential

@Composable
internal fun AppNavigationRoute(
    onExit: () -> Unit,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit = {},
    isSessionInProgress: Boolean = false,
    hasSessionError: Boolean = false,
    onSessionErrorDismissed: () -> Unit = {},
    onSessionRetry: (() -> Unit)? = null,
    onSignUpComplete: (() -> Unit)? = null,
    onLogout: () -> Unit = {},
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: AppNavigationDestination = AppNavigationDestination.Login,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappAuthColor.background,
    ) { innerPadding ->
        AppNavHost(
            onExit = onExit,
            onGoogleCredentialReceived = onGoogleCredentialReceived,
            isSessionInProgress = isSessionInProgress,
            hasSessionError = hasSessionError,
            onSessionErrorDismissed = onSessionErrorDismissed,
            onSessionRetry = onSessionRetry,
            onSignUpComplete = onSignUpComplete,
            onLogout = onLogout,
            isLoggingOut = isLoggingOut,
            hasLogoutError = hasLogoutError,
            onLogoutErrorDismissed = onLogoutErrorDismissed,
            startDestination = startDestination,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
        )
    }
}
