package com.yapp.plus.feature.setting.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Notification reload button", widthDp = 393, showBackground = true)
@Composable
private fun NotificationRetryRowPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsNotificationRetryRow(onRetry = {})
    }
}

@Preview(name = "Notification reload disabled", widthDp = 320, showBackground = true)
@Composable
private fun NotificationRetryRowDisabledPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsNotificationRetryRow(onRetry = {}, isRetrying = true)
    }
}
