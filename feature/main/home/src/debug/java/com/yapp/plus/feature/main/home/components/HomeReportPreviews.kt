package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Preview(widthDp = 393, showBackground = true)
@Composable
fun HomeReportTypePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeReportSheet(
            HomeInteractionUiState(
                sheet = HomeSheet.ReportType,
                reportType = AttendanceReportType.LATE,
            ),
            {
            },
            {},
            {},
            Modifier.padding(20.dp),
        )
    }
}

@Preview(widthDp = 393, showBackground = true)
@Composable
fun HomeReportReasonPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeReportSheet(
            HomeInteractionUiState(
                sheet = HomeSheet.ReportReason,
                reportType = AttendanceReportType.EARLY_LEAVE,
                reportReason = "개인 일정",
            ),
            {
            },
            {},
            {},
            Modifier.padding(20.dp),
        )
    }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun HomeReportOptionPreview() {
    YappTheme { HomeReportOption(AttendanceReportType.LATE, true, {}) }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun HomeReportConfirmationPreview() {
    YappTheme { HomeReportConfirmation({}) }
}
