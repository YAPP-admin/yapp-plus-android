package com.yapp.plus.core.designsystem.component.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Auth layout", widthDp = 393, heightDp = 756)
@Preview(name = "Compact auth layout", widthDp = 320, heightDp = 480)
@Composable
private fun AuthLayoutPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            AuthLayout(
                onBack = {},
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
                bottom = {
                    AuthPrimaryAction(
                        text = stringResource(R.string.yapp_preview_auth_next),
                        onClick = {},
                        isEnabled = true,
                    )
                },
            ) {
                AuthHeading(
                    text = stringResource(R.string.yapp_preview_auth_heading),
                    modifier = Modifier.padding(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AuthHeadingPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthHeading(
            text = stringResource(R.string.yapp_preview_auth_heading),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AuthGraphicPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthGraphic(resource = R.drawable.graphic_point_construction)
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AuthPrimaryActionPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column {
            for (isEnabled in listOf(true, false)) {
                AuthPrimaryAction(
                    text = stringResource(R.string.yapp_preview_auth_next),
                    onClick = {},
                    isEnabled = isEnabled,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 756)
@Composable
private fun AuthErrorDialogPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthErrorDialog(
            title = stringResource(R.string.yapp_preview_auth_error_title),
            body = stringResource(R.string.yapp_preview_auth_error_body),
            dismissText = stringResource(R.string.yapp_preview_auth_close),
            reportText = stringResource(R.string.yapp_preview_auth_report),
            onDismiss = {},
            onReport = {},
        )
    }
}
