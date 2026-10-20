package com.yapp.plus.core.designsystem.components.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Main header", widthDp = 393, showBackground = true)
@Composable
private fun YappMainHeaderPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappMainHeader(
            title = stringResource(R.string.yapp_preview_navigation_my),
            iconResource = R.drawable.ic_settings,
            iconDescription = stringResource(R.string.yapp_preview_settings),
        )
    }
}
