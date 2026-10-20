package com.yapp.plus.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNotificationUiModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal object HomePreviewData {
    val now: LocalDateTime = LocalDateTime.of(2026, 11, 3, 12, 0)

    @Composable
    fun session(status: AttendanceStatusUiModel = AttendanceStatusUiModel.NotOpen) =
        CurrentWeekSessionUiModel(
            weekStartDateLabel = stringResource(R.string.attendance_home_demo_week_start),
            weekEndDateLabel = stringResource(R.string.attendance_home_demo_week_end),
            dateLabel = stringResource(R.string.attendance_home_demo_session_date),
            name = stringResource(R.string.home_preview_session_name),
            startTimeLabel = stringResource(R.string.attendance_home_demo_session_time),
            location = stringResource(R.string.attendance_home_demo_session_location),
            attendanceStatus = status,
            sessionId = 42,
            weekStartDate = LocalDate.of(2026, 11, 1),
            sessionDate = LocalDate.of(2026, 11, 7),
            startTime = LocalTime.of(14, 0),
        )

    @Composable
    fun state(status: AttendanceStatusUiModel = AttendanceStatusUiModel.NotOpen) =
        AttendanceUiState.Content(session(status))

    @Composable
    fun notices(): List<HomeNoticeUiModel> =
        listOf(
            HomeNoticeUiModel(
                1,
                stringResource(R.string.home_preview_notice_session),
                isSessionNotice = true,
            ),
            HomeNoticeUiModel(
                2,
                stringResource(R.string.home_preview_notice_survey),
                isSessionNotice = false,
            ),
            HomeNoticeUiModel(
                3,
                stringResource(R.string.home_preview_notice_staff),
                isSessionNotice = false,
            ),
        )

    @Composable
    fun notifications(): List<HomeNotificationUiModel> =
        listOf(
            HomeNotificationUiModel(
                1,
                stringResource(R.string.home_preview_notification_session),
                stringResource(R.string.home_preview_notification_date),
            ),
            HomeNotificationUiModel(
                2,
                stringResource(R.string.home_preview_notification_place),
                stringResource(R.string.home_preview_notification_date),
            ),
            HomeNotificationUiModel(
                3,
                stringResource(R.string.home_preview_notification_notice),
                stringResource(R.string.home_preview_notification_date),
            ),
        )
}
