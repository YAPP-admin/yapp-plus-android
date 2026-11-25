package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.HomeSubmissionStage

@Preview(name = "Attendance code sheet", widthDp = 393, heightDp = 790)
@Composable
fun AttendanceCodeModalPreview() {
    HomeModalPreview(HomeInteractionUiState(sheet = HomeSheet.AttendanceCode))
}

@Preview(name = "Attendance location check", widthDp = 393, heightDp = 790)
@Composable
fun AttendanceLocationCheckingPreview() {
    HomeModalPreview(
        HomeInteractionUiState(
            sheet = HomeSheet.AttendanceCode,
            code = "1234",
            submissionStage = HomeSubmissionStage.CheckingLocation,
        ),
        isCheckingLocation = true,
    )
}

@Preview(name = "Attendance location recovery", widthDp = 393, heightDp = 790)
@Composable
fun AttendancePrecisePermissionPreview() {
    HomeModalPreview(
        HomeInteractionUiState(
            sheet = HomeSheet.AttendanceCode,
            code = "1234",
        ),
        locationIssue = HomeAttendanceLocationIssue.PrecisePermissionRequired,
    )
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

@Composable
private fun HomeModalPreview(
    state: HomeInteractionUiState,
    isCheckingLocation: Boolean = false,
    locationIssue: HomeAttendanceLocationIssue? = null,
) {
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
                HomeSheetContent(
                    state = state,
                    onCodeChange = {},
                    onConfirm = {},
                    isCheckingLocation = isCheckingLocation,
                    locationIssue = locationIssue,
                )
            }
        }
    }
}
