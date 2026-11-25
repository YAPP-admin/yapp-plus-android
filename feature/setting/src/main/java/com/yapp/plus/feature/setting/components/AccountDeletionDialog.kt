package com.yapp.plus.feature.setting.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialogVariant
import com.yapp.plus.core.designsystem.theme.YappMainColor
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
            isDeleting -> null
            hasError -> stringResource(R.string.settings_account_delete_error_title)
            else -> stringResource(R.string.settings_account_delete_confirmation)
        }
    val body =
        when {
            isDeleting -> null
            hasError -> stringResource(R.string.settings_account_delete_error_body)
            else -> stringResource(R.string.settings_account_withdrawal_notice)
        }
    YappAlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = title,
        variant = YappAlertDialogVariant.Compact,
        compactWidth = 290.dp,
        body = body,
        confirmText =
            stringResource(
                if (hasError) R.string.settings_confirm else R.string.settings_account_delete,
            ),
        dismissText = if (hasError) null else stringResource(R.string.settings_cancel),
        showActions = !isDeleting,
        content = if (isDeleting) ({ AccountDeletionProgressIndicator() }) else null,
        onConfirm = {
            if (hasError) {
                onDismiss()
            } else if (!isDeleting) {
                onConfirm()
            }
        },
        onDismiss = { if (!isDeleting) onDismiss() },
    )
}

@Composable
private fun AccountDeletionProgressIndicator() {
    val description = stringResource(R.string.settings_account_deleting)
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = YappMainColor.brand,
            strokeWidth = 3.dp,
        )
    }
}
