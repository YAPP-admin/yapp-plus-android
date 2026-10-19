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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialCancellationException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onBack: () -> Unit,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current.applicationContext
    val activity = LocalActivity.current
    val serverClientId = stringResource(R.string.google_server_client_id)
    val credentialManager =
        remember(context) {
            GoogleCredentialManager(CredentialManager.create(context))
        }
    val coroutineScope = rememberCoroutineScope()
    val currentOnGoogleCredentialReceived by rememberUpdatedState(onGoogleCredentialReceived)
    var isLoginInProgress by remember { mutableStateOf(false) }
    var isErrorDialogVisible by remember { mutableStateOf(false) }
    var isTokenIssuedDialogVisible by remember { mutableStateOf(false) }

    LoginContent(
        onBack = onBack,
        onGoogleLogin = {
            if (!isLoginInProgress) {
                val currentActivity = activity
                if (currentActivity == null) {
                    isErrorDialogVisible = true
                } else {
                    isLoginInProgress = true
                    coroutineScope.launch {
                        try {
                            val credential =
                                credentialManager.getGoogleLoginCredential(
                                    activity = currentActivity,
                                    serverClientId = serverClientId,
                                )
                            currentOnGoogleCredentialReceived(credential)
                            isTokenIssuedDialogVisible = true
                        } catch (_: GetCredentialCancellationException) {
                            // Dismissing the Google account chooser is a normal user action.
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (_: Exception) {
                            isErrorDialogVisible = true
                        } finally {
                            isLoginInProgress = false
                        }
                    }
                }
            }
        },
        modifier = modifier,
        isLoginEnabled = !isLoginInProgress,
    )

    if (isErrorDialogVisible) {
        LoginErrorDialog(
            onDismiss = { isErrorDialogVisible = false },
            onReport = { isErrorDialogVisible = false },
        )
    }

    if (isTokenIssuedDialogVisible) {
        GoogleIdTokenIssuedDialog(
            onDismiss = { isTokenIssuedDialogVisible = false },
        )
    }
}
