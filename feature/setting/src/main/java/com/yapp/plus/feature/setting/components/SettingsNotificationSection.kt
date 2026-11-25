package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import com.yapp.plus.feature.setting.NotificationSettingsUiState

@Composable
fun SettingsNotificationSection(
    state: NotificationSettingsUiState,
    onEnabledChange: (Boolean) -> Unit,
) {
    SettingsNotificationRow(
        enabled = state.isEnabled == true && state.isPermissionGranted,
        onEnabledChange = onEnabledChange,
    )
}
