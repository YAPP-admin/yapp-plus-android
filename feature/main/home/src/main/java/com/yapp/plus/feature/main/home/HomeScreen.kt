package com.yapp.plus.feature.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.components.HomeEmptyState
import com.yapp.plus.feature.main.home.components.HomeNoticeList
import com.yapp.plus.feature.main.home.components.HomeQuickActions
import com.yapp.plus.feature.main.home.components.HomeReportTip
import com.yapp.plus.feature.main.home.components.HomeSessionCard
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import java.time.LocalDateTime
import java.time.ZoneId
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun HomeScreen(
    state: AttendanceUiState<CurrentWeekSessionUiModel>,
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onRetry: () -> Unit = {},
    onSessionsClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onScoreClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    notices: List<HomeNoticeUiModel> = emptyList(),
    hasAttended: Boolean = false,
    hasReported: Boolean = false,
    now: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")),
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.background)) {
        YappMainHeader(
            title = stringResource(R.string.attendance_home_title),
            iconResource = DesignSystemR.drawable.ic_notification,
            iconDescription = stringResource(R.string.attendance_home_notifications),
            onIconClick = onNotificationsClick,
            horizontalPadding = 16.dp,
            iconTint = YappHomeColor.subtle,
            leadingContent = {
                Image(
                    painterResource(DesignSystemR.drawable.graphic_yapp_logo),
                    stringResource(R.string.attendance_home_title),
                    Modifier.size(width = 80.dp, height = 30.dp),
                )
            },
        )
        BoxWithConstraints(Modifier.weight(1f)) {
            val minHeight = maxHeight
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = minHeight)
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = if (notices.isEmpty()) 0.dp else 14.dp),
            ) {
                HomeSessionCard(
                    state = state,
                    hasAttended = hasAttended,
                    hasReported = hasReported,
                    now = now,
                    onAttendanceClick = onAttendanceClick,
                    onRetry = onRetry,
                )
                Spacer(Modifier.height(10.dp))
                val session = (state as? AttendanceUiState.Content)?.value
                val canReport =
                    session != null &&
                        session.attendanceStatus != AttendanceStatusUiModel.NotEligible
                HomeQuickActions(
                    hasAttended = hasAttended,
                    canReport = canReport,
                    onSessionsClick = onSessionsClick,
                    onReportClick = onReportClick,
                    onScoreClick = onScoreClick,
                )
                Spacer(Modifier.height(40.dp))
                Box(Modifier.weight(1f).fillMaxWidth().heightIn(min = 220.dp)) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        YappText(
                            stringResource(R.string.home_week_notices),
                            YappHomeTypography.heading,
                            YappHomeColor.text,
                        )
                        if (notices.isEmpty()) {
                            HomeEmptyState(
                                message = stringResource(R.string.home_notices_empty),
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                            )
                        } else {
                            HomeNoticeList(notices, onNoticeClick)
                        }
                    }
                    if (!hasAttended &&
                        !hasReported &&
                        canReport &&
                        session?.sessionDate?.isAfter(now.toLocalDate()) == true
                    ) {
                        HomeReportTip(
                            Modifier.align(Alignment.TopEnd).offset(x = (-32).dp, y = (-31).dp),
                        )
                    }
                }
            }
        }
    }
}
