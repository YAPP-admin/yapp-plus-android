package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Demo data label", showBackground = true)
@Composable
private fun DemoDataLabelPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        DemoDataLabel()
    }
}

@Preview(name = "Attendance history card", widthDp = 353)
@Composable
private fun AttendanceHistoryCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceHistoryCard()
    }
}

@Preview(name = "Attendance history item", widthDp = 353)
@Composable
private fun AttendanceHistoryItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceHistoryItem(
            title = stringResource(R.string.attendance_home_history_session),
            date = stringResource(R.string.attendance_home_history_date),
        )
    }
}
