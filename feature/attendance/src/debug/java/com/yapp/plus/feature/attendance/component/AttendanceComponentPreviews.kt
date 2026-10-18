package com.yapp.plus.feature.attendance.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.attendance.R
import com.yapp.plus.feature.attendance.model.AttendanceScheduleSessionUiModel
import com.yapp.plus.feature.attendance.model.AttendanceScheduleStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceScheduleTypeUiModel
import com.yapp.plus.feature.attendance.model.AttendanceStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceUiState
import com.yapp.plus.feature.attendance.model.CurrentWeekSessionUiModel
import com.yapp.plus.core.designsystem.R as DesignSystemR

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

@Preview(name = "Schedule detail", widthDp = 250, showBackground = true)
@Composable
private fun AttendanceDetailPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceDetail(
            text = stringResource(R.string.attendance_schedule_place_first),
            iconResource = DesignSystemR.drawable.ic_map_pin,
        )
    }
}

@Preview(name = "Schedule item today", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceScheduleItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleItem(
            session = sampleScheduleSession(AttendanceScheduleStatusUiModel.Today),
        )
    }
}

@Preview(name = "Schedule item upcoming", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceScheduleUpcomingItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleItem(
            session = sampleScheduleSession(AttendanceScheduleStatusUiModel.Upcoming),
        )
    }
}

@Preview(name = "Schedule item past", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceSchedulePastItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleItem(
            session = sampleScheduleSession(AttendanceScheduleStatusUiModel.Past),
        )
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
    )

@Composable
private fun sampleScheduleSession(status: AttendanceScheduleStatusUiModel) =
    AttendanceScheduleSessionUiModel(
        dateLabel = stringResource(R.string.attendance_schedule_date_second),
        name = stringResource(R.string.attendance_schedule_planning),
        location = stringResource(R.string.attendance_schedule_place_second),
        timeLabel = stringResource(R.string.attendance_schedule_time_second),
        type = AttendanceScheduleTypeUiModel.Regular,
        status = status,
    )

@Preview(name = "Profile", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceProfileCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceProfileCard(
            name = stringResource(R.string.attendance_my_name),
            role = stringResource(R.string.attendance_my_role),
            generation = stringResource(R.string.attendance_my_generation),
        )
    }
}

@Preview(name = "Attendance summary", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceSummaryCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceSummaryCard() }
}

@Preview(name = "Attendance statistic", widthDp = 85, showBackground = true)
@Composable
private fun AttendanceStatisticPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceStatistic(label = stringResource(R.string.attendance_my_present), count = 3)
    }
}

@Preview(name = "My menu item", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceMenuItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceMenuItem(title = stringResource(R.string.attendance_my_history))
    }
}
