package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Confirm account deletion")
@Composable
private fun AccountDeletionConfirmationPreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = false, hasError = false, onConfirm = {}, onDismiss = {})
    }
}

@Preview(name = "Account deletion in progress")
@Composable
private fun AccountDeletionProgressPreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = true, hasError = false, onConfirm = {}, onDismiss = {})
    }
}

@Preview(name = "Account deletion failed")
@Composable
private fun AccountDeletionFailurePreview() {
    YappTheme {
        AccountDeletionDialog(isDeleting = false, hasError = true, onConfirm = {}, onDismiss = {})
    }
}
