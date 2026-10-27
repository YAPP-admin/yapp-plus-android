package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Confirm YAPP withdrawal")
@Composable
private fun AccountDeletionConfirmationPreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = false, hasError = false, onConfirm = {}, onDismiss = {})
    }
}

@Preview(name = "Withdrawal in progress")
@Composable
private fun AccountDeletionProgressPreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = true, hasError = false, onConfirm = {}, onDismiss = {})
    }
}

@Preview(name = "Withdrawal failed")
@Composable
private fun AccountDeletionFailurePreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = false, hasError = true, onConfirm = {}, onDismiss = {})
    }
}
