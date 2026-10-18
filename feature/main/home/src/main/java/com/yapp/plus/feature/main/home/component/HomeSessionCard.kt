package com.yapp.plus.feature.main.home.component

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.button.YappButton
import com.yapp.plus.core.designsystem.component.button.YappButtonSize
import com.yapp.plus.core.designsystem.component.button.YappButtonVariant
import com.yapp.plus.core.designsystem.component.main.YappIconLabel
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun HomeSessionCard(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onAttendanceClick: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappMainColor.brand,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YappText(
                stringResource(R.string.attendance_home_session_label),
                YappTypography.label1NormalMedium,
                YappMainColor.surface,
            )
            when (state) {
                AttendanceUiState.Loading -> HomeSessionLoading()
                AttendanceUiState.Empty ->
                    YappText(
                        stringResource(R.string.attendance_home_session_empty),
                        YappTypography.heading1Bold,
                        YappMainColor.surface,
                    )

                AttendanceUiState.Error -> HomeSessionError(onRetry = onRetry)
                is AttendanceUiState.Content ->
                    HomeSessionContent(
                        session = state.value,
                        onAttendanceClick = onAttendanceClick,
                    )
            }
        }
    }
}

@Composable
private fun HomeSessionLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().height(112.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = YappMainColor.surface,
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun HomeSessionError(onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        YappText(
            stringResource(R.string.attendance_home_session_error),
            YappTypography.body1NormalMedium,
            YappMainColor.surface,
        )
        YappButton(
            text = stringResource(R.string.attendance_retry),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.SolidSecondary,
            size = YappButtonSize.Medium,
        )
    }
}

@Composable
private fun HomeSessionContent(
    session: CurrentWeekSessionUiModel,
    onAttendanceClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YappText(
                stringResource(
                    R.string.attendance_home_week_range,
                    session.weekStartDateLabel,
                    session.weekEndDateLabel,
                ),
                YappTypography.caption1Bold,
                YappMainColor.surface,
            )
            YappText(session.name, YappTypography.heading1Bold, YappMainColor.surface)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            YappIconLabel(
                text = session.dateLabel,
                iconResource = DesignSystemR.drawable.ic_navigation_calendar,
                tint = YappMainColor.surface,
            )
            YappIconLabel(
                text = session.startTimeLabel,
                iconResource = DesignSystemR.drawable.ic_clock,
                tint = YappMainColor.surface,
            )
            session.location?.let { location ->
                YappIconLabel(
                    text = location,
                    iconResource = DesignSystemR.drawable.ic_map_pin,
                    tint = YappMainColor.surface,
                )
            }
        }
        YappText(
            text = stringResource(session.attendanceStatus.messageResource),
            style = YappTypography.caption1Bold,
            color = YappMainColor.surface,
        )
        Surface(
            onClick = onAttendanceClick,
            enabled = session.attendanceStatus == AttendanceStatusUiModel.Available,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(
                        if (session.attendanceStatus == AttendanceStatusUiModel.Available) {
                            1f
                        } else {
                            0.6f
                        },
                    ),
            color = YappMainColor.surface,
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    stringResource(R.string.attendance_home_session_action),
                    YappTypography.label1NormalBold,
                    YappMainColor.brand,
                )
            }
        }
    }
}

private val AttendanceStatusUiModel.messageResource: Int
    get() =
        when (this) {
            AttendanceStatusUiModel.NotEligible -> R.string.attendance_status_not_eligible
            AttendanceStatusUiModel.NotOpen -> R.string.attendance_status_not_open
            AttendanceStatusUiModel.Available -> R.string.attendance_status_available
        }
