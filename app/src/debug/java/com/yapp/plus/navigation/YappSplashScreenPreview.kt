package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "YAPP splash", showSystemUi = true, widthDp = 393, heightDp = 852)
@Composable
private fun YappSplashScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappSplashScreen()
    }
}
