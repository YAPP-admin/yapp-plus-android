package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.ReportChoice

@Preview(name = "Attendance code sheet", widthDp = 393, heightDp = 790)
@Composable
fun AttendanceCodeModalPreview() {
    HomeModalPreview(HomeInteractionUiState(sheet = HomeSheet.AttendanceCode))
}

@Preview(name = "Attendance invalid code", widthDp = 393, heightDp = 790)
@Composable
fun AttendanceCodeErrorModalPreview() {
    HomeModalPreview(
        HomeInteractionUiState(
            sheet = HomeSheet.AttendanceCode,
            code = "1234",
            isInvalidCode = true,
            errorResource = R.string.home_code_invalid,
        ),
    )
}

@Preview(name = "Report selection", widthDp = 393, heightDp = 790)
@Composable
fun ReportChoiceModalPreview() {
    HomeModalPreview(
        HomeInteractionUiState(sheet = HomeSheet.ReportKind, reportChoice = ReportChoice.Absent),
    )
}

@Preview(name = "Late report duration", widthDp = 393, heightDp = 790)
@Composable
fun ReportLateModalPreview() {
    HomeModalPreview(
        HomeInteractionUiState(
            sheet = HomeSheet.LateDuration,
            reportChoice = ReportChoice.Late,
            reportType = AttendanceReportType.ONE_HOUR_LATE,
        ),
    )
}

@Preview(name = "Report reason", widthDp = 393, heightDp = 790)
@Composable
fun ReportReasonModalPreview() {
    HomeModalPreview(
        HomeInteractionUiState(sheet = HomeSheet.ReportReason, reportChoice = ReportChoice.Absent),
    )
}

@Composable
private fun HomeModalPreview(state: HomeInteractionUiState) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Box(Modifier.fillMaxSize()) {
            HomeScreen(HomePreviewData.state(), now = HomePreviewData.now)
            YappBottomSheet(
                onDismissRequest = {},
                onConfirm = {},
                onDismiss = {},
                title = null,
                body = null,
                confirmText = null,
                dismissText = null,
                variant = YappBottomSheetVariant.Home,
                showHandle = true,
                showActions = false,
            ) {
                HomeSheetContent(state, {}, {}, {}, {}, {})
            }
        }
    }
}
