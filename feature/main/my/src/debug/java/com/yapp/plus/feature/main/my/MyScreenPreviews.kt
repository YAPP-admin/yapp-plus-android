package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState

@Preview(name = "My page", widthDp = 393, heightDp = 692)
@Preview(name = "Compact My page", widthDp = 320, heightDp = 560)
@Composable
private fun MyScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyScreen(state = MyPreviewData.state, onContactClick = {})
    }
}

@Preview(name = "My loading", widthDp = 393, heightDp = 692)
@Composable
private fun MyLoadingPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyScreen(
            state =
                MyUiState(
                    profile = MyProfileUiState.Loading(savedName = MyPreviewData.profile.name),
                    isLoading = true,
                ),
        )
    }
}

@Preview(name = "Attendance history", widthDp = 393, heightDp = 756)
@Composable
private fun MyHistoryScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyAttendanceHistoryScreen(MyPreviewData.history, {}, {}, {})
    }
}

@Preview(name = "Attendance history loading", widthDp = 393, heightDp = 756)
@Composable
private fun MyHistoryLoadingPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyAttendanceHistoryScreen(MyPreviewData.history, {}, {}, {}, isScreenLoading = true)
    }
}
