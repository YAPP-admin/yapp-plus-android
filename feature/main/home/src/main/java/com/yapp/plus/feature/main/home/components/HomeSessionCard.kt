package com.yapp.plus.feature.main.home.components

import androidx.annotation.StringRes
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
import com.yapp.plus.feature.main.home.model.homeSessionStartFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun HomeSessionCard(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onAttendanceClick: () -> Unit = {},
    onScoreClick: () -> Unit = {},
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
                AttendanceUiState.Inactive ->
                    YappText(
                        stringResource(R.string.session_inactive),
                        YappHomeTypography.title,
                        YappHomeColor.text,
                        Modifier.padding(vertical = 24.dp),
                    )
                AttendanceUiState.Empty ->
                    YappText(
                        stringResource(R.string.attendance_home_session_empty),
                        YappHomeTypography.title,
                        YappHomeColor.text,
                        Modifier.padding(vertical = 24.dp),
                    )
                AttendanceUiState.Error -> HomeSessionError()
                is AttendanceUiState.Content ->
                    HomeSessionContent(
                        session = state.value,
                        now = now,
                        onAttendanceClick = onAttendanceClick,
                        onScoreClick = onScoreClick,
                    )
            }
        }
    }
}

@Composable
internal fun HomeSessionError(
    @StringRes messageResource: Int = R.string.attendance_home_session_error,
) {
    YappText(
        stringResource(messageResource),
        YappHomeTypography.body,
        YappHomeColor.muted,
    )
}

@Composable
internal fun HomeSessionContent(
    session: CurrentWeekSessionUiModel,
    now: LocalDateTime,
    onAttendanceClick: () -> Unit,
    onScoreClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HomeSessionVenue(location = session.location, address = session.address)
        val titleResource =
            if (session.sessionDate == now.toLocalDate()) {
                R.string.home_today_title
            } else {
                R.string.home_week_title
            }
        YappText(
            stringResource(titleResource, session.name.removeSuffix("세션").trim()),
            YappHomeTypography.title,
            YappHomeColor.text,
        )
    }
    session.weekStartDate?.let {
        HomeWeekCalendar(it, session.sessionDate, now.toLocalDate())
    }
    val attendanceStatus = session.attendanceStatus
    val isCheckedIn = attendanceStatus == AttendanceStatusUiModel.CheckedIn
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (isCheckedIn) {
            YappText(
                stringResource(attendanceStatus.messageResource),
                YappHomeTypography.label,
                YappHomeColor.text,
            )
        }
        YappButton(
            text = attendanceButtonText(session, now),
            onClick = if (isCheckedIn) onScoreClick else onAttendanceClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isCheckedIn || attendanceStatus == AttendanceStatusUiModel.Available,
            variant = YappButtonVariant.HomeAttendance,
            size = YappButtonSize.HomeSecondary,
        )
    }
}

@Composable
internal fun HomeSessionVenue(
    location: String?,
    address: String?,
) {
    val venue = listOfNotNull(location, address).joinToString("\n")
    Row(
        verticalAlignment = if (address == null) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (venue.isNotEmpty()) {
            Icon(
                painterResource(DesignSystemR.drawable.ic_map_pin),
                null,
                Modifier
                    .padding(top = if (address == null) 0.dp else 2.dp)
                    .size(14.dp),
                YappHomeColor.placeholder,
            )
        }
        YappText(
            venue.ifEmpty { stringResource(R.string.attendance_home_session_label) },
            YappHomeTypography.label,
            YappHomeColor.subtle,
        )
    }
}

@Composable
private fun attendanceButtonText(
    session: CurrentWeekSessionUiModel,
    now: LocalDateTime,
): String =
    when (val status = session.attendanceStatus) {
        AttendanceStatusUiModel.CheckedIn -> stringResource(R.string.home_score_check)
        AttendanceStatusUiModel.Available -> stringResource(R.string.attendance_home_session_action)
        AttendanceStatusUiModel.NotOpen -> attendanceNotOpenText(session, now)
        AttendanceStatusUiModel.NotEligible,
        AttendanceStatusUiModel.PendingApproval,
        -> stringResource(status.messageResource)
    }

@Composable
private fun attendanceNotOpenText(
    session: CurrentWeekSessionUiModel,
    now: LocalDateTime,
): String =
    when {
        session.sessionDate?.isAfter(now.toLocalDate()) == true ->
            stringResource(
                R.string.home_session_starts,
                session.startTime?.let { session.sessionDate.atTime(it).format(homeSessionStartFormat) }
                    ?: "${session.sessionDate.dayOfMonth}일 ${session.startTimeLabel}",
            )
        session.sessionDate == now.toLocalDate() &&
            session.startTime?.isAfter(now.toLocalTime()) == true ->
            stringResource(
                R.string.home_attendance_opens,
                session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            )
        else -> stringResource(R.string.attendance_status_not_open)
    }
