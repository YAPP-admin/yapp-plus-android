package com.yapp.plus.feature.login

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Kakao login", widthDp = 393, heightDp = 756)
@Preview(name = "Compact login", widthDp = 320, heightDp = 480)
@Composable
private fun LoginContentPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            LoginContent(
                onBack = {},
                onKakaoLogin = {},
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
            )
        }
    }
}

@Preview(name = "Login error", widthDp = 393, heightDp = 756)
@Composable
private fun LoginErrorDialogPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            LoginContent(
                onBack = {},
                onKakaoLogin = {},
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
            )
            LoginErrorDialog(onDismiss = {}, onReport = {})
        }
    }
}
