package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialogVariant
import com.yapp.plus.feature.setting.R

@Composable
internal fun AccountDeletionDialog(
    isDeleting: Boolean,
    hasError: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title =
        when {
            isDeleting -> R.string.settings_account_deleting
            hasError -> R.string.settings_account_delete_error_title
            else -> R.string.settings_account_delete_confirmation
        }
    val body =
        when {
            isDeleting -> null
            hasError -> R.string.settings_account_delete_error_body
            else -> R.string.settings_account_withdrawal_notice
        }
    YappAlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = stringResource(title),
        variant = YappAlertDialogVariant.Compact,
        compactWidth = 290.dp,
        body = body?.let { stringResource(it) },
        confirmText =
            stringResource(
                if (hasError) R.string.settings_retry else R.string.settings_account_delete,
            ),
        dismissText = stringResource(R.string.settings_cancel),
        showActions = !isDeleting,
        onConfirm = { if (!isDeleting) onConfirm() },
        onDismiss = { if (!isDeleting) onDismiss() },
    )
}
