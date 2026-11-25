package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappHomeColor
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
fun HomeReportReasonPreview() = HomeReportReasonPreviewContent("개인 일정")

@Preview(widthDp = 393, showBackground = true)
@Composable
fun HomeReportReasonEmptyPreview() = HomeReportReasonPreviewContent("")

@Composable
private fun HomeReportReasonPreviewContent(reason: String) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeReportSheet(
            HomeInteractionUiState(
                sheet = HomeSheet.ReportReason,
                reportType = AttendanceReportType.EARLY_LEAVE,
                reportReason = reason,
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

@Preview(name = "Report submitted", widthDp = 393, heightDp = 320, showBackground = true)
@Composable
private fun HomeReportConfirmationPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(YappHomeColor.background)
                    .safeDrawingPadding(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            HomeReportConfirmation(onDismiss = {})
        }
    }
}
