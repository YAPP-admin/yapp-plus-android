package com.yapp.plus.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.component.auth.AuthErrorDialog

@Composable
fun LoginErrorDialog(
    onDismiss: () -> Unit,
    onReport: () -> Unit,
) {
    AuthErrorDialog(
        title = stringResource(R.string.auth_error_title),
        body = stringResource(R.string.auth_error_description),
        reportText = stringResource(R.string.auth_report),
        dismissText = stringResource(R.string.auth_close),
        onReport = onReport,
        onDismiss = onDismiss,
    )
}
