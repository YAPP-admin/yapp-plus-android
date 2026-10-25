package com.yapp.plus.feature.main.board.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.board.R
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Preview(name = "Board preparing", showBackground = true, widthDp = 393)
@Composable
private fun BoardUnavailableNoticePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        BoardNoticeState<Nothing>(
            state = NoticeUiState.Unavailable,
            onRetry = {},
            emptyMessage = R.string.board_empty,
        ) {}
    }
}
