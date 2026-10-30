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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.components.HomeNoticeContent
import com.yapp.plus.feature.main.home.components.HomeQuickActions
import com.yapp.plus.feature.main.home.components.HomeSessionCard
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
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
    onScoreClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    noticeState: HomeNoticeUiState = HomeNoticeUiState.Empty,
    onNoticeRetry: () -> Unit = {},
    hasAttended: Boolean = false,
    now: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")),
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.background)) {
        YappMainHeader(
            title = stringResource(R.string.attendance_home_title),
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
                    .padding(top = 14.dp, bottom = 14.dp),
            ) {
                HomeSessionCard(
                    state = state,
                    hasAttended = hasAttended,
                    now = now,
                    onAttendanceClick = onAttendanceClick,
                    onRetry = onRetry,
                )
                Spacer(Modifier.height(10.dp))
                HomeQuickActions(
                    onSessionsClick = onSessionsClick,
                    onScoreClick = onScoreClick,
                    onReportClick = onReportClick,
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
                        HomeNoticeContent(
                            noticeState,
                            onNoticeClick,
                            onNoticeRetry,
                            Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
