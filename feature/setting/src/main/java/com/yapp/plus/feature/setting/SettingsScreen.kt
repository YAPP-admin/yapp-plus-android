package com.yapp.plus.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.setting.components.AccountDeletionDialog
import com.yapp.plus.feature.setting.components.SettingsDivider
import com.yapp.plus.feature.setting.components.SettingsHeader
import com.yapp.plus.feature.setting.components.SettingsLinkRow

@Composable
fun SettingsScreen(
    appVersion: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    isDeletingAccount: Boolean = false,
    hasDeleteAccountError: Boolean = false,
    onDeleteAccountErrorDismissed: () -> Unit = {},
) {
    var activeDialog by rememberSaveable { mutableStateOf<SettingsDialog?>(null) }
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUri = stringResource(R.string.settings_privacy_policy_url)
    val termsUri = stringResource(R.string.settings_terms_url)
    val deletionContactUri = stringResource(R.string.settings_account_delete_contact_uri)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        SettingsHeader(onBackClick = onBack)
        SettingsLinkRow(
            title = stringResource(R.string.settings_privacy_policy),
            onClick = { runCatching { uriHandler.openUri(privacyPolicyUri) } },
        )
        SettingsDivider()
        SettingsLinkRow(
            title = stringResource(R.string.settings_terms),
            onClick = { runCatching { uriHandler.openUri(termsUri) } },
        )
        SettingsDivider()
        SettingsLinkRow(
            title = stringResource(R.string.settings_account_withdrawal),
            onClick = { activeDialog = SettingsDialog.AccountWithdrawal },
            enabled = !isLoggingOut && !isDeletingAccount,
        )
        SettingsDivider()
        SettingsLinkRow(
            title = stringResource(R.string.settings_account_delete_contact),
            onClick = { runCatching { uriHandler.openUri(deletionContactUri) } },
        )
        SelectionContainer {
            YappText(
                text = stringResource(R.string.settings_account_delete_email),
                modifier = Modifier.padding(horizontal = 20.dp),
                style = YappTypography.body2NormalRegular,
                color = YappMainColor.textSecondary,
            )
        }

        Spacer(Modifier.weight(1f))
        YappButton(
            text =
                stringResource(
                    if (isLoggingOut) R.string.settings_logging_out else R.string.settings_logout,
                ),
            onClick = { activeDialog = SettingsDialog.Logout },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
            variant = YappButtonVariant.OutlinedSecondary,
            size = YappButtonSize.Large,
            enabled = !isLoggingOut && !isDeletingAccount,
        )
        YappText(
            text = stringResource(R.string.settings_app_version, appVersion),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
            style = YappTypography.body2NormalRegular,
            color = YappMainColor.textFaint,
            textAlign = TextAlign.Center,
        )
    }

    when {
        isDeletingAccount || hasDeleteAccountError || activeDialog == SettingsDialog.AccountWithdrawal -> {
            AccountDeletionDialog(
                isDeleting = isDeletingAccount,
                hasError = hasDeleteAccountError,
                onConfirm = {
                    activeDialog = null
                    onDeleteAccount()
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
                title = stringResource(R.string.settings_logout),
                body = stringResource(R.string.settings_logout_confirmation),
                confirmText = stringResource(R.string.settings_logout),
                dismissText = stringResource(R.string.settings_cancel),
                onConfirm = {
                    activeDialog = null
                    onLogout()
                },
                onDismiss = { activeDialog = null },
            )
        }
    }
}
