package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.setting.NotificationSettingsUiState

@Preview(name = "Device notifications enabled", widthDp = 393)
@Composable
private fun NotificationEnabledPreview() {
    NotificationPreview(NotificationSettingsUiState(isEnabled = true))
}

@Preview(name = "Device notifications disabled", widthDp = 320)
@Composable
private fun NotificationDisabledPreview() {
    NotificationPreview(NotificationSettingsUiState(isEnabled = false))
}

@Preview(name = "Loading notification preference", widthDp = 393)
@Composable
private fun NotificationLoadingPreview() {
    NotificationPreview(NotificationSettingsUiState())
}

@Preview(name = "Updating notification preference", widthDp = 393)
@Composable
private fun NotificationUpdatingPreview() {
    NotificationPreview(NotificationSettingsUiState(isEnabled = true, isUpdating = true))
}

@Preview(name = "Notification load failure", widthDp = 320)
@Composable
private fun NotificationErrorPreview() {
    NotificationPreview(
        NotificationSettingsUiState(
            hasLoadError = true,
            isPermissionGranted = false,
        ),
    )
}

@Preview(name = "Notification retry in progress", widthDp = 393)
@Composable
private fun NotificationRetryingPreview() {
    NotificationPreview(NotificationSettingsUiState(hasLoadError = true, isLoading = true))
}

@Preview(name = "Notification update failure", widthDp = 393)
@Composable
private fun NotificationUpdateErrorPreview() {
    NotificationPreview(NotificationSettingsUiState(isEnabled = true, hasUpdateError = true))
}

@Composable
private fun NotificationPreview(state: NotificationSettingsUiState) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsNotificationSection(
            state = state,
            onEnabledChange = {},
            onRetry = {},
            onOpenSettings = {},
        )
    }
}
