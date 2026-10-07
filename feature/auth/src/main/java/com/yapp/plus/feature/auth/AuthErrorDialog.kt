package com.yapp.plus.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialogVariant

/** Visibility and report handling belong to the caller. */
@Composable
fun AuthErrorDialog(
    onDismiss: () -> Unit,
    onReport: () -> Unit,
) {
    YappAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.auth_error_title),
        body = stringResource(R.string.auth_error_description),
        confirmText = stringResource(R.string.auth_report),
        dismissText = stringResource(R.string.auth_close),
        onConfirm = onReport,
        onDismiss = onDismiss,
        variant = YappAlertDialogVariant.Compact,
    )
}
