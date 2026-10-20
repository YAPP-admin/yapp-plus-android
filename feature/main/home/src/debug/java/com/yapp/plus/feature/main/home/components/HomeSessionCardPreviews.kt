package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel

@Preview(name = "Home session card", widthDp = 361)
@Composable
private fun HomeSessionCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeSessionCard(state = AttendanceUiState.Content(sampleCurrentWeekSession()))
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
                AttendanceUiState.Error,
            ).forEach { HomeSessionCard(state = it) }
        }
    }
}

@Composable
private fun sampleCurrentWeekSession() =
    CurrentWeekSessionUiModel(
        weekStartDateLabel = stringResource(R.string.attendance_home_demo_week_start),
        weekEndDateLabel = stringResource(R.string.attendance_home_demo_week_end),
        dateLabel = stringResource(R.string.attendance_home_demo_session_date),
        name = stringResource(R.string.attendance_home_demo_session_name),
        startTimeLabel = stringResource(R.string.attendance_home_demo_session_time),
        location = stringResource(R.string.attendance_home_demo_session_location),
        attendanceStatus = AttendanceStatusUiModel.Available,
        sessionId = 42,
        weekStartDate = java.time.LocalDate.of(2026, 10, 18),
        sessionDate = java.time.LocalDate.of(2026, 10, 24),
        startTime = java.time.LocalTime.of(14, 0),
    )
