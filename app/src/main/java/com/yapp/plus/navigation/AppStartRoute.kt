package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.feature.login.LoginRoute

@Composable
internal fun AppStartRoute(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppAuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginState = uiState as? AppAuthUiState.Login
    AppStartContent(uiState = uiState, modifier = modifier) { startDestination ->
        AuthDemoRoute(
            onExit = onExit,
            onSignUpComplete = viewModel::completeSignUp,
            startDestination = startDestination,
            modifier = modifier,
            loginContent = { onBack ->
                LoginRoute(
                    onBack = onBack,
                    onGoogleCredentialReceived = viewModel::signInWithGoogle,
                    isSessionInProgress = loginState?.isSigningIn == true,
                    hasSessionError =
                        loginState?.hasError == true ||
                            uiState == AppAuthUiState.RestoreError,
                    onSessionErrorDismissed = viewModel::dismissError,
                    onSessionRetry =
                        if (uiState == AppAuthUiState.RestoreError) {
                            viewModel::retrySessionRestore
                        } else {
                            null
                        },
                )
            },
        )
    }
}

@Composable
internal fun AppStartContent(
    uiState: AppAuthUiState,
    modifier: Modifier = Modifier,
    content: @Composable (AuthDemoDestination) -> Unit,
) {
    if (uiState == AppAuthUiState.Restoring) {
        YappSplashScreen(modifier = modifier)
    } else {
        val session =
            when (uiState) {
                is AppAuthUiState.Authenticated -> uiState.session
                is AppAuthUiState.SignUpRequired -> uiState.session
                else -> null
            }
        val startDestination =
            when {
                session == null -> AuthDemoDestination.Login
                uiState is AppAuthUiState.SignUpRequired -> AuthDemoDestination.Name
                else -> AuthDemoDestination.Main
            }
        key(session?.userId, uiState is AppAuthUiState.SignUpRequired) {
            content(startDestination)
        }
    }
}
