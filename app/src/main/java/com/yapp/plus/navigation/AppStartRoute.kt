package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun AppStartRoute(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppAuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginState = uiState as? AppAuthUiState.Login
    val authenticatedState = uiState as? AppAuthUiState.Authenticated
    AppStartContent(uiState = uiState, modifier = modifier) { startDestination ->
        AppNavigationRoute(
            onExit = onExit,
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
            onSignUpComplete = viewModel::completeSignUp,
            onLogout = viewModel::signOut,
            isLoggingOut = authenticatedState?.isSigningOut == true,
            hasLogoutError = authenticatedState?.hasSignOutError == true,
            onLogoutErrorDismissed = viewModel::dismissSignOutError,
            onDeleteAccount = viewModel::deleteAccount,
            isDeletingAccount = authenticatedState?.isDeletingAccount == true,
            hasDeleteAccountError = authenticatedState?.hasDeleteAccountError == true,
            onDeleteAccountErrorDismissed = viewModel::dismissDeleteAccountError,
            startDestination = startDestination,
            modifier = modifier,
        )
    }
}

@Composable
internal fun AppStartContent(
    uiState: AppAuthUiState,
    modifier: Modifier = Modifier,
    content: @Composable (AppNavigationDestination) -> Unit,
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
                session == null -> AppNavigationDestination.Login
                uiState is AppAuthUiState.SignUpRequired -> AppNavigationDestination.SignUp
                else -> AppNavigationDestination.Main
            }
        key(session?.userId, uiState is AppAuthUiState.SignUpRequired) {
            content(startDestination)
        }
    }
}
