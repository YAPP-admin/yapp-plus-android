package com.yapp.plus.feature.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Settings", widthDp = 393, heightDp = 852)
@Preview(name = "Compact settings", widthDp = 320, heightDp = 560)
@Composable
private fun SettingsScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsScreen(
            onBack = {},
            onLogout = {},
        )
    }
}

@Preview(name = "Settings without authentication", widthDp = 393, heightDp = 852)
@Composable
private fun SettingsUnavailableActionsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsScreen(onBack = {}, onLogout = null)
    }
}
