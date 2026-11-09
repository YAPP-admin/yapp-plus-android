package com.yapp.plus.feature.setting.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.setting.NotificationSettingsUiState
import com.yapp.plus.feature.setting.R

@Composable
fun SettingsNotificationSection(
    state: NotificationSettingsUiState,
    onEnabledChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column {
        SettingsNotificationRow(
            enabled = state.isEnabled == true,
            onEnabledChange = onEnabledChange,
            isInteractive = state.isEnabled != null && !state.isUpdating,
        )
        val statusMessage =
            when {
                state.isUpdating -> R.string.settings_notifications_updating
                state.hasError && state.isEnabled == null -> R.string.settings_notifications_load_error
                state.hasError -> R.string.settings_notifications_error
                state.isEnabled == null -> R.string.settings_notifications_loading
                else -> null
            }
        if (statusMessage != null) {
            YappText(
                text = stringResource(statusMessage),
                style = YappTypography.scaleT3Medium,
                color = YappMainColor.textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }
        if (state.hasError) {
            SettingsLinkRow(
                stringResource(R.string.settings_notifications_retry),
                onRetry,
                enabled = !state.isUpdating,
            )
        }
        if (!state.isPermissionGranted) {
            SettingsLinkRow(
                stringResource(R.string.settings_notifications_permission),
                onOpenSettings,
                enabled = !state.isUpdating,
            )
        }
    }
}
