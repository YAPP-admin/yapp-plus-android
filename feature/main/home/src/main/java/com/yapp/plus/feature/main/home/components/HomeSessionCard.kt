package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun HomeSessionCard(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onAttendanceClick: () -> Unit = {},
    onRetry: () -> Unit = {},
    hasAttended: Boolean = false,
    hasReported: Boolean = false,
    now: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappHomeColor.surface,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (state) {
                AttendanceUiState.Loading ->
                    Box(
                        Modifier.fillMaxWidth().height(179.dp),
                        Alignment.Center,
                    ) { CircularProgressIndicator(color = YappHomeColor.brand) }
                AttendanceUiState.Empty ->
                    YappText(
                        stringResource(R.string.attendance_home_session_empty),
                        YappHomeTypography.title,
                        YappHomeColor.text,
                        Modifier.padding(vertical = 24.dp),
                    )
                AttendanceUiState.Error -> HomeSessionError(onRetry)
                is AttendanceUiState.Content ->
                    HomeSessionContent(
                        session = state.value,
                        hasAttended = hasAttended,
                        hasReported = hasReported,
                        now = now,
                        onAttendanceClick = onAttendanceClick,
                    )
            }
        }
    }
}

@Composable
internal fun HomeSessionError(onRetry: () -> Unit) {
    YappText(
        stringResource(R.string.attendance_home_session_error),
        YappHomeTypography.body,
        YappHomeColor.muted,
    )
    YappButton(
        text = stringResource(R.string.attendance_retry),
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth(),
        variant = YappButtonVariant.SolidNeutral,
        size = YappButtonSize.HomeSecondary,
    )
}

@Composable
internal fun HomeSessionContent(
    session: CurrentWeekSessionUiModel,
    hasAttended: Boolean,
    hasReported: Boolean,
    now: LocalDateTime,
    onAttendanceClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (session.location != null) {
                Icon(
                    painterResource(DesignSystemR.drawable.ic_map_pin),
                    null,
                    Modifier.size(14.dp),
                    YappHomeColor.placeholder,
                )
            }
            YappText(
                session.location ?: stringResource(R.string.attendance_home_session_label),
                YappHomeTypography.label,
                YappHomeColor.subtle,
            )
        }
        val titleResource =
            if (session.sessionDate == now.toLocalDate()) {
                R.string.home_today_title
            } else {
                R.string.home_week_title
            }
        val title =
            if (hasReported && session.sessionDate?.isAfter(now.toLocalDate()) == true) {
                stringResource(
                    R.string.home_next_session,
                    java.time.temporal.ChronoUnit.DAYS
                        .between(now.toLocalDate(), session.sessionDate),
                )
            } else {
                stringResource(titleResource, session.name.removeSuffix("세션").trim())
            }
        YappText(
            title,
            YappHomeTypography.title,
            YappHomeColor.text,
        )
    }
    session.weekStartDate?.let {
        HomeWeekCalendar(it, session.sessionDate, now.toLocalDate())
    }
    YappButton(
        text = attendanceButtonText(session, hasAttended, now),
        onClick = onAttendanceClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !hasAttended && session.attendanceStatus == AttendanceStatusUiModel.Available,
        variant = YappButtonVariant.SolidNeutral,
        size = YappButtonSize.HomeSecondary,
    )
}

@Composable
internal fun attendanceButtonText(
    session: CurrentWeekSessionUiModel,
    hasAttended: Boolean,
    now: LocalDateTime,
): String =
    when {
        hasAttended -> stringResource(R.string.home_attended)
        session.attendanceStatus == AttendanceStatusUiModel.Available ->
            stringResource(R.string.attendance_home_session_action)
        session.attendanceStatus == AttendanceStatusUiModel.NotEligible ->
            stringResource(R.string.attendance_status_not_eligible)
        session.sessionDate?.isAfter(now.toLocalDate()) == true ->
            stringResource(
                R.string.home_session_starts,
                session.sessionDate.format(DateTimeFormatter.ofPattern("M/d")) + " " +
                    (
                        session.startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ?: session.startTimeLabel
                    ),
            )
        session.sessionDate == now.toLocalDate() &&
            session.startTime?.isAfter(now.toLocalTime()) == true ->
            stringResource(
                R.string.home_attendance_opens,
                session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            )
        else -> stringResource(R.string.attendance_status_not_open)
    }
