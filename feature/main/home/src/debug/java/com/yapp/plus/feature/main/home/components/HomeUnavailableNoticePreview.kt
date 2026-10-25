package com.yapp.plus.feature.main.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState

@Preview(name = "Notice preparing", showBackground = true, widthDp = 393)
@Composable
private fun HomeUnavailableNoticePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeNoticeContent(HomeNoticeUiState.Unavailable, onNoticeClick = {}, onRetry = {})
    }
}
