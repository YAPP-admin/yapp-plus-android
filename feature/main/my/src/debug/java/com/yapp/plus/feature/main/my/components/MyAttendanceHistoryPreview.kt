package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryItem
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState

@Preview(name = "My attendance history", widthDp = 393)
@Composable
private fun MyAttendanceHistoryPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column(Modifier.background(YappMainColor.surface)) {
            MyAttendanceHistory(
                state =
                    MyAttendanceHistoryState.Content(
                        listOf(
                            MyAttendanceHistoryItem(
                                sessionName = "YAPP 테스트 세션",
                                checkedInAtLabel = "2026.10.20 (화) 오후 7:00",
                            ),
                        ),
                    ),
                onRetry = {},
            )
        }
    }
}
