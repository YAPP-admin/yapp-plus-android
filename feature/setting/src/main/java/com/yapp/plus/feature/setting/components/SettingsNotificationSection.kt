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
        if (state.hasLoadError) {
            SettingsNotificationRetryRow(onRetry = onRetry, isRetrying = state.isLoading)
        } else {
            SettingsNotificationRow(
                enabled = state.isEnabled == true,
                onEnabledChange = onEnabledChange,
                isInteractive = state.isEnabled != null && !state.isUpdating && !state.isLoading,
            )
        }
        val statusMessage =
            when {
                state.isLoading -> R.string.settings_notifications_loading
                state.hasLoadError -> R.string.settings_notifications_load_error
                state.isUpdating -> R.string.settings_notifications_updating
                state.hasUpdateError -> R.string.settings_notifications_error
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
        if (state.hasUpdateError && !state.hasLoadError) {
            SettingsLinkRow(
                stringResource(R.string.settings_notifications_retry),
                onRetry,
                enabled = !state.isUpdating && !state.isLoading,
            )
        }
        if (!state.isPermissionGranted) {
            SettingsLinkRow(
                stringResource(R.string.settings_notifications_permission),
                onOpenSettings,
                enabled = !state.isUpdating && !state.isLoading,
            )
        }
    }
}
