package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState

@Preview(name = "My page", widthDp = 393, heightDp = 692)
@Preview(name = "Tall My page", widthDp = 393, heightDp = 852)
@Preview(name = "Compact My page", widthDp = 320, heightDp = 560)
@Composable
private fun MyScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyScreen(
            state =
                MyUiState(
                    profile = MyProfileUiState.Unavailable,
                    attendanceHistory = MyAttendanceHistoryState.Unavailable,
                    isAttendanceHistoryExpanded = true,
                ),
        )
    }
}

@Preview(name = "Profile preparing", widthDp = 393)
@Composable
private fun MyProfileUnavailablePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyProfileContent(state = MyProfileUiState.Unavailable, onRetry = {})
    }
}
