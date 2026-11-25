package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.R

@Preview(name = "Home session card", widthDp = 361)
@Composable
private fun HomeSessionCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSessionCard(state = AttendanceUiState.Content(sampleSession()))
    }
}

@Preview(name = "Home session card states", widthDp = 361, heightDp = 600)
@Composable
private fun HomeSessionCardStatesPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column {
            listOf(
                AttendanceUiState.Loading,
                AttendanceUiState.Empty,
                AttendanceUiState.Inactive,
                AttendanceUiState.Inactive,
                AttendanceUiState.Error,
            ).forEach { HomeSessionCard(state = it) }
        }
    }
}

@Composable
private fun sampleSession(status: AttendanceStatusUiModel = AttendanceStatusUiModel.Available) =
    CurrentWeekSessionUiModel(
        weekStartDateLabel = stringResource(R.string.attendance_home_demo_week_start),
        weekEndDateLabel = stringResource(R.string.attendance_home_demo_week_end),
        dateLabel = stringResource(R.string.attendance_home_demo_session_date),
        name = stringResource(R.string.attendance_home_demo_session_name),
        startTimeLabel = stringResource(R.string.attendance_home_demo_session_time),
        location = stringResource(R.string.attendance_home_demo_session_location),
        attendanceStatus = status,
        sessionId = 42,
        weekStartDate = java.time.LocalDate.of(2026, 10, 18),
        sessionDate = java.time.LocalDate.of(2026, 10, 24),
        startTime = java.time.LocalTime.of(14, 0),
        address = stringResource(R.string.attendance_home_demo_session_address),
    )

@Preview(name = "Home attendance completed", widthDp = 361)
@Composable
private fun HomeAttendanceCompletedPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSessionCard(
            state =
                AttendanceUiState.Content(
                    sampleSession(AttendanceStatusUiModel.CheckedIn).copy(
                        name = stringResource(R.string.attendance_home_demo_completed_session_name),
                    ),
                ),
        )
    }
}

@Preview(name = "Home attendance pending approval", widthDp = 361)
@Composable
private fun HomeAttendancePendingApprovalPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSessionCard(
            state =
                AttendanceUiState.Content(
                    sampleSession(AttendanceStatusUiModel.PendingApproval),
                ),
        )
    }
}
