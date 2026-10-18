package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.attendance.model.AttendanceScheduleSessionUiModel
import com.yapp.plus.feature.attendance.model.AttendanceScheduleStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceScheduleTypeUiModel
import com.yapp.plus.feature.attendance.model.AttendanceUiState

@Preview(name = "Schedule loading", widthDp = 393, heightDp = 692)
@Composable
private fun AttendanceScheduleLoadingPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleScreen(state = AttendanceUiState.Loading)
    }
}

@Preview(name = "Schedule empty", widthDp = 393, heightDp = 692)
@Composable
private fun AttendanceScheduleEmptyPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleScreen(state = AttendanceUiState.Empty)
    }
}

@Preview(name = "Schedule error", widthDp = 393, heightDp = 692)
@Composable
private fun AttendanceScheduleErrorPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleScreen(state = AttendanceUiState.Error)
    }
}

@Preview(name = "Schedule content", widthDp = 393, heightDp = 692)
@Composable
private fun AttendanceScheduleContentPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleScreen(
            state = AttendanceUiState.Content(listOf(sampleScheduleSession())),
        )
    }
}

@Composable
private fun sampleScheduleSession() =
    AttendanceScheduleSessionUiModel(
        dateLabel = stringResource(R.string.attendance_schedule_date_second),
        name = stringResource(R.string.attendance_schedule_planning),
        location = stringResource(R.string.attendance_schedule_place_second),
        timeLabel = stringResource(R.string.attendance_schedule_time_second),
        type = AttendanceScheduleTypeUiModel.Regular,
        status = AttendanceScheduleStatusUiModel.Upcoming,
    )
