package com.yapp.plus.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential

@Composable
internal fun AppNavigationRoute(
    onExit: () -> Unit,
    onGoogleCredentialReceived: (suspend (GoogleLoginCredential) -> AuthResult)? = null,
    onRegistrationCompleted: () -> Unit = {},
    registrationToken: String? = null,
    modifier: Modifier = Modifier,
    startDestination: AppNavigationDestination? = null,
) {
    val authViewModel: AppAuthViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    if (authUiState.isCheckingSession && startDestination == null) {
        YappSplashScreen(modifier = modifier)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = YappAuthColor.background,
        ) { innerPadding ->
            AppNavHost(
                onExit = onExit,
                onGoogleCredentialReceived = onGoogleCredentialReceived ?: authViewModel::authenticateWithGoogle,
                onLogout = authViewModel::logout,
                isLoggingOut = authUiState.isLoggingOut,
                hasLogoutError = authUiState.hasLogoutError,
                onLogoutErrorDismissed = authViewModel::onLogoutErrorDismissed,
                onDeleteAccount = authViewModel::withdraw,
                isDeletingAccount = authUiState.isWithdrawing,
                hasDeleteAccountError = authUiState.hasWithdrawalError,
                onDeleteAccountErrorDismissed = authViewModel::onWithdrawalErrorDismissed,
                onRegistrationCompleted = {
                    authViewModel.onRegistrationCompleted()
                    onRegistrationCompleted()
                },
                registrationToken = registrationToken ?: authUiState.registrationToken,
                sessionAuthenticated = authUiState.hasSession,
                startDestination =
                    startDestination
                        ?: if (authUiState.hasSession) {
                            AppNavigationDestination.Main
                        } else {
                            AppNavigationDestination.Login
                        },
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
            )
        }
    }
}
