package com.yapp.plus.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialogVariant
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.setting.components.AccountDeletionDialog
import com.yapp.plus.feature.setting.components.SettingsHeader
import com.yapp.plus.feature.setting.components.SettingsLinkRow
import com.yapp.plus.feature.setting.components.SettingsNotificationRow

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: (() -> Unit)?,
    onDeleteAccount: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    isDeletingAccount: Boolean = false,
    hasDeleteAccountError: Boolean = false,
    onDeleteAccountErrorDismissed: () -> Unit = {},
) {
    var activeDialog by rememberSaveable { mutableStateOf<SettingsDialog?>(null) }
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUri = stringResource(R.string.settings_privacy_policy_url)
    val termsUri = stringResource(R.string.settings_terms_url)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        SettingsHeader(onBackClick = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            SettingsNotificationRow(
                enabled = notificationsEnabled,
                onEnabledChange = { notificationsEnabled = it },
            )
            SettingsLinkRow(
                title = stringResource(R.string.settings_privacy_policy),
                onClick = { runCatching { uriHandler.openUri(privacyPolicyUri) } },
            )
            SettingsLinkRow(
                title = stringResource(R.string.settings_terms),
                onClick = { runCatching { uriHandler.openUri(termsUri) } },
            )
            SettingsLinkRow(
                title = stringResource(R.string.settings_account_withdrawal),
                onClick = { activeDialog = SettingsDialog.AccountWithdrawal },
                enabled = onDeleteAccount != null && !isLoggingOut && !isDeletingAccount,
            )
        }

        YappButton(
            text =
                stringResource(
                    if (isLoggingOut) R.string.settings_logging_out else R.string.settings_logout,
                ),
            onClick = { activeDialog = SettingsDialog.Logout },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ).height(58.dp),
            variant = YappButtonVariant.OutlinedAssistive,
            size = YappButtonSize.Settings,
            enabled = onLogout != null && !isLoggingOut && !isDeletingAccount,
        )
    }

    when {
        isDeletingAccount ||
            hasDeleteAccountError ||
            activeDialog == SettingsDialog.AccountWithdrawal -> {
            AccountDeletionDialog(
                isDeleting = isDeletingAccount,
                hasError = hasDeleteAccountError,
                onConfirm = {
                    activeDialog = null
                    onDeleteAccount?.invoke()
                },
                onDismiss = {
                    activeDialog = null
                    onDeleteAccountErrorDismissed()
                },
            )
        }

        hasLogoutError -> {
            YappAlertDialog(
                onDismissRequest = onLogoutErrorDismissed,
                title = stringResource(R.string.settings_logout_error_title),
                body = stringResource(R.string.settings_logout_error_body),
                confirmText = stringResource(R.string.settings_confirm),
                dismissText = null,
                onConfirm = onLogoutErrorDismissed,
                onDismiss = onLogoutErrorDismissed,
            )
        }

        activeDialog == SettingsDialog.Logout -> {
            YappAlertDialog(
                onDismissRequest = { activeDialog = null },
                title = stringResource(R.string.settings_logout_confirmation),
                body = null,
                variant = YappAlertDialogVariant.Compact,
                compactWidth = 272.dp,
                confirmText = stringResource(R.string.settings_logout),
                dismissText = stringResource(R.string.settings_cancel),
                onConfirm = {
                    activeDialog = null
                    onLogout?.invoke()
                },
                onDismiss = { activeDialog = null },
            )
        }
    }
}
