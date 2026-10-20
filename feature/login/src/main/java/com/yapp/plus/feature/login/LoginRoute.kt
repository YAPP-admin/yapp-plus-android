package com.yapp.plus.feature.login

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.feature.login.components.LoginErrorDialog
import com.yapp.plus.feature.login.model.LoginUiState
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit,
    modifier: Modifier = Modifier,
    isSessionInProgress: Boolean = false,
    hasSessionError: Boolean = false,
    onSessionErrorDismissed: () -> Unit = {},
    onSessionRetry: (() -> Unit)? = null,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val serverClientId = stringResource(R.string.google_server_client_id)
    val currentOnGoogleCredentialReceived by rememberUpdatedState(onGoogleCredentialReceived)

    val coroutineScope = rememberCoroutineScope()
    val requestLogin: () -> Unit = {
        if (!isSessionInProgress) {
            onSessionErrorDismissed()
            coroutineScope.launch {
                viewModel
                    .onGoogleLogin(activity, serverClientId)
                    ?.let(currentOnGoogleCredentialReceived)
            }
        }
    }

    LoginContent(
        onBack = onBack,
        onGoogleLogin = requestLogin,
        modifier = modifier,
        isLoginEnabled = !uiState.isLoginInProgress && !isSessionInProgress,
    )

    if (uiState.dialog == LoginUiState.Dialog.Error || hasSessionError) {
        LoginErrorDialog(
            onDismiss = {
                viewModel.dismissDialog()
                onSessionErrorDismissed()
            },
            onRetry = {
                viewModel.dismissDialog()
                if (hasSessionError && onSessionRetry != null) onSessionRetry() else requestLogin()
            },
        )
    }
}
