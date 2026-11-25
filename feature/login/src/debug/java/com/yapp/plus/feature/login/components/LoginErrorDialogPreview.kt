package com.yapp.plus.feature.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Login error dialog", widthDp = 393, heightDp = 756)
@Composable
private fun LoginErrorDialogPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        LoginErrorDialog(
            onDismiss = {},
            onReport = {},
        )
    }
}
