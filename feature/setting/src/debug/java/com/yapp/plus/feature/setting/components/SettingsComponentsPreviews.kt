package com.yapp.plus.feature.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Settings components", widthDp = 393)
@Composable
private fun SettingsComponentsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column(Modifier.background(YappMainColor.surface)) {
            SettingsHeader(onBackClick = {})
            SettingsNotificationRow(enabled = true, onEnabledChange = {})
            SettingsDivider()
            SettingsLinkRow(title = "개인정보 처리 방침", onClick = {})
            SettingsDivider()
        }
    }
}

@Preview(widthDp = 393, showBackground = true)
@Composable
private fun SettingsHeaderPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { SettingsHeader({}) }
}

@Preview(widthDp = 393, showBackground = true)
@Composable
private fun SettingsLinkRowPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        SettingsLinkRow(title = "개인정보 처리 방침", onClick = {})
    }
}
