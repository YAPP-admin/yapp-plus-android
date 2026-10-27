package com.yapp.plus.feature.login

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
    onGoogleCredentialReceived: suspend (GoogleLoginCredential) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val onAuthenticationResult: suspend (GoogleLoginCredential) -> Boolean = { credential ->
        onGoogleCredentialReceived(credential)
        true
    }
    LoginRoute(
        onBack = onBack,
        onGoogleCredentialReceived = onAuthenticationResult,
        modifier = modifier,
    )
}

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onGoogleCredentialReceived: suspend (GoogleLoginCredential) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val serverClientId = stringResource(R.string.google_server_client_id)
    val currentOnGoogleCredentialReceived by rememberUpdatedState(onGoogleCredentialReceived)
    var isAuthenticationInProgress by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val requestLogin: () -> Unit = {
        if (!isAuthenticationInProgress) {
            isAuthenticationInProgress = true
            coroutineScope.launch {
                try {
                    val credential = viewModel.onGoogleLogin(activity, serverClientId) ?: return@launch
                    if (!currentOnGoogleCredentialReceived(credential)) {
                        viewModel.showAuthenticationError()
                    }
                } catch (exception: kotlinx.coroutines.CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    viewModel.showAuthenticationError()
                } finally {
                    isAuthenticationInProgress = false
                }
            }
        }
    }

    LoginContent(
        onBack = onBack,
        onGoogleLogin = requestLogin,
        modifier = modifier,
        isLoginEnabled = !uiState.isLoginInProgress && !isAuthenticationInProgress,
    )

    if (uiState.dialog == LoginUiState.Dialog.Error) {
        LoginErrorDialog(
            onDismiss = viewModel::dismissDialog,
            onRetry = {
                viewModel.dismissDialog()
                requestLogin()
            },
        )
    }
}
