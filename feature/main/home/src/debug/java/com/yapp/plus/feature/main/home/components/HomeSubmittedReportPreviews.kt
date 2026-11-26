package com.yapp.plus.feature.main.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.attendance.AttendanceReportType

@Preview(showBackground = true)
@Composable
fun HomeSubmittedReportPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSubmittedReportSheet(
            type = AttendanceReportType.LATE,
            reason = "병원 진료 후 바로 출발할게요.",
            onDismiss = {},
        )
    }
}

@Preview(name = "Submitted report without reason", showBackground = true)
@Composable
fun HomeSubmittedReportLegacyPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSubmittedReportSheet(
            type = AttendanceReportType.LATE,
            reason = null,
            onDismiss = {},
        )
    }
}
