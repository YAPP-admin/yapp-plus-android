package com.yapp.plus.core.designsystem.components.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(widthDp = 361, showBackground = true)
@Composable
private fun SettingsButtonPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappButton(
            text = "로그아웃",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.OutlinedAssistive,
            size = YappButtonSize.Settings,
        )
    }
}
