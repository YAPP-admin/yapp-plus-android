package com.yapp.plus.feature.login

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val serverClientId = stringResource(R.string.google_server_client_id)
    val currentOnGoogleCredentialReceived by rememberUpdatedState(onGoogleCredentialReceived)

    LaunchedEffect(viewModel) {
        viewModel.credentialEvents.collect { credential ->
            currentOnGoogleCredentialReceived(credential)
        }
    }

    LoginContent(
        onBack = onBack,
        onGoogleLogin = {
            viewModel.onGoogleLogin(
                activity = activity,
                serverClientId = serverClientId,
            )
        },
        modifier = modifier,
        isLoginEnabled = !uiState.isLoginInProgress,
    )

    when (uiState.dialog) {
        LoginUiState.Dialog.Error -> {
            LoginErrorDialog(
                onDismiss = viewModel::dismissDialog,
                onReport = viewModel::dismissDialog,
            )
        }
        LoginUiState.Dialog.GoogleIdTokenIssued -> {
            GoogleIdTokenIssuedDialog(onDismiss = viewModel::dismissDialog)
        }
        null -> Unit
    }
}
