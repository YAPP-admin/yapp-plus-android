package com.yapp.plus.core.designsystem.component.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.component.popup.YappAlertButtonLayout
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialogVariant

@Composable
fun AuthErrorDialog(
    title: String,
    body: String,
    dismissText: String,
    reportText: String,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    YappAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = title,
        body = body,
        confirmText = reportText,
        dismissText = dismissText,
        buttonLayout = YappAlertButtonLayout.Horizontal,
        onConfirm = onReport,
        onDismiss = onDismiss,
        variant = YappAlertDialogVariant.Compact,
    )
}
