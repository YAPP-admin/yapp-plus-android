package com.yapp.plus.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialog

@Composable
internal fun GoogleIdTokenIssuedDialog(onDismiss: () -> Unit) {
    YappAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.auth_google_token_title),
        body = stringResource(R.string.auth_google_token_description),
        confirmText = stringResource(R.string.auth_close),
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
    )
}
