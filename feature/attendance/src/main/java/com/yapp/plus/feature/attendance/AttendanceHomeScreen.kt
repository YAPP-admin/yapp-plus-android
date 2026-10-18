package com.yapp.plus.feature.attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.attendance.component.AttendanceHistoryCard
import com.yapp.plus.feature.attendance.component.DemoDataLabel
import com.yapp.plus.feature.attendance.component.HomeSessionCard
import com.yapp.plus.feature.attendance.model.AttendanceStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceUiState
import com.yapp.plus.feature.attendance.model.CurrentWeekSessionUiModel
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun AttendanceHomeScreen(
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
) {
    AttendanceHomeScreen(
        state = demoCurrentWeekSessionUiState(),
        modifier = modifier,
        onNotificationsClick = onNotificationsClick,
        onAttendanceClick = onAttendanceClick,
    )
}

@Composable
fun AttendanceHomeScreen(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface)
                .verticalScroll(rememberScrollState()),
    ) {
        YappMainHeader(
            title = stringResource(R.string.attendance_home_title),
            iconResource = DesignSystemR.drawable.ic_notification,
            iconDescription = stringResource(R.string.attendance_home_notifications),
            onIconClick = onNotificationsClick,
            horizontalPadding = 16.dp,
            leadingContent = {
                Image(
                    painter = painterResource(DesignSystemR.drawable.graphic_yapp_logo),
                    contentDescription = stringResource(R.string.attendance_home_title),
                    modifier = Modifier.size(width = 80.dp, height = 30.dp),
                )
            },
        )
        HomeSessionCard(
            state = state,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp),
            onAttendanceClick = onAttendanceClick,
            onRetry = onRetry,
        )
        AttendanceHistoryCard(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
        )
        DemoDataLabel(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 20.dp),
            textResource = R.string.attendance_home_history_demo_data,
        )
    }
}

@Composable
private fun demoCurrentWeekSessionUiState(): AttendanceUiState<CurrentWeekSessionUiModel> =
    AttendanceUiState.Content(
        CurrentWeekSessionUiModel(
            weekStartDateLabel = stringResource(R.string.attendance_home_demo_week_start),
            weekEndDateLabel = stringResource(R.string.attendance_home_demo_week_end),
            dateLabel = stringResource(R.string.attendance_home_demo_session_date),
            name = stringResource(R.string.attendance_home_demo_session_name),
            startTimeLabel = stringResource(R.string.attendance_home_demo_session_time),
            location = stringResource(R.string.attendance_home_demo_session_location),
            attendanceStatus = AttendanceStatusUiModel.Available,
        ),
    )
