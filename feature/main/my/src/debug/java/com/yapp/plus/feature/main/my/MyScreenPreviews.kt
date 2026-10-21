package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryItem
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyProfileSummaryState
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
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
                    submittedProfile = MyProfileUiState.Content(MyProfileUiModel("홍길동", "01012345678")),
                    testProfile = MyProfileSummaryState.Content("홍길동"),
                    attendanceHistory =
                        MyAttendanceHistoryState.Content(
                            listOf(
                                MyAttendanceHistoryItem("YAPP 28기 세션", "2026.10.17 (토) 오후 2:00"),
                                MyAttendanceHistoryItem("YAPP 28기 세션", "2026.10.10 (토) 오후 2:00"),
                            ),
                        ),
                    isAttendanceHistoryExpanded = true,
                ),
        )
    }
}
