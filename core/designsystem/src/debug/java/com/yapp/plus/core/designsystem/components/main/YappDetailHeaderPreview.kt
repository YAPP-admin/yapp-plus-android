package com.yapp.plus.core.designsystem.components.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(widthDp = 393, showBackground = true)
@Composable
private fun YappDetailHeaderPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappDetailHeader(title = "세션", backDescription = "뒤로 가기", onBack = {})
    }
}
