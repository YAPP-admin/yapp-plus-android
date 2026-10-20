package com.yapp.plus.feature.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.feature.login.R

@Composable
fun LoginErrorDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    YappAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.auth_error_title),
        body = stringResource(R.string.auth_error_description),
        confirmText = stringResource(R.string.auth_retry),
        dismissText = stringResource(R.string.auth_close),
        onConfirm = onRetry,
        onDismiss = onDismiss,
    )
}
