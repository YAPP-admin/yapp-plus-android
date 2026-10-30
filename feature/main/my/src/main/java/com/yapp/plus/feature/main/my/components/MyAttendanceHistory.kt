package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryItem
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun MyAttendanceHistory(
    state: MyAttendanceHistoryState.Content,
    showAbsenceWarning: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val headerHeight = 183.dp + if (showAbsenceWarning) 52.dp else 0.dp
        val emptyHeight = (maxHeight - headerHeight).coerceAtLeast(240.dp)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = if (state.items.isEmpty()) 0.dp else 24.dp,
                ),
        ) {
            item {
                MyStatsCard(
                    title = stringResource(R.string.my_session_summary),
                    valueLabel = stringResource(R.string.my_progress),
                    value =
                        state.summary.progressPercent?.let {
                            stringResource(R.string.my_percent, it)
                        } ?: stringResource(R.string.my_empty_value),
                    entries =
                        listOf(
                            stringResource(R.string.my_total_sessions) to
                                stringResource(
                                    R.string.my_session_count,
                                    state.summary.totalSessionCount,
                                ),
                            stringResource(R.string.my_remaining_sessions) to
                                stringResource(
                                    R.string.my_session_count,
                                    state.summary.remainingSessionCount,
                                ),
                        ),
                )
                if (showAbsenceWarning) {
                    Spacer(Modifier.height(10.dp))
                    MyAbsenceWarning()
                }
                Spacer(Modifier.height(40.dp))
                YappText(
                    stringResource(R.string.my_session_history),
                    YappTypography.scaleT6Bold,
                    YappMainColor.textPrimary,
                )
                if (state.items.isNotEmpty()) Spacer(Modifier.height(12.dp))
            }
            if (state.items.isEmpty() && state.nextCursor == null) {
                item { MyHistoryEmpty(Modifier.height(emptyHeight)) }
            }
            items(state.items, key = { it.sessionId }) { MyHistoryRow(it) }
            if (state.nextCursor != null) {
                item {
                    MyHistoryPagination(state, onLoadMore)
                }
            }
        }
    }
}

@Composable
internal fun MyHistoryRow(item: MyAttendanceHistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        YappText(
            text = item.sessionName,
            style = YappTypography.scaleT5Bold,
            color = YappMainColor.textPrimary,
            modifier = Modifier.weight(1f),
        )
        MyAttendanceBadge(item.status)
    }
}

@Composable
internal fun MyHistoryEmpty(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().heightIn(min = 240.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Image(
                painterResource(R.drawable.graphic_attendance_empty),
                contentDescription = null,
                modifier = Modifier.size(width = 90.dp, height = 74.dp),
            )
            YappText(
                stringResource(R.string.my_attendance_history_empty),
                YappTypography.scaleT5Regular,
                YappMainColor.textSubtle,
            )
        }
    }
}

@Composable
internal fun MyAbsenceWarning() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.layerFill, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(DesignSystemR.drawable.ic_status_info),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = YappMainColor.textFaint,
        )
        YappText(
            stringResource(R.string.my_absence_warning),
            YappTypography.scaleT3Medium,
            YappMainColor.textSecondary,
        )
    }
}

@Composable
internal fun MyHistoryPagination(
    state: MyAttendanceHistoryState.Content,
    onLoadMore: () -> Unit,
) {
    LaunchedEffect(state.nextCursor) {
        if (!state.hasLoadMoreError) onLoadMore()
    }
    MyRequestState(
        message =
            stringResource(
                if (state.hasLoadMoreError) {
                    R.string.my_more_error
                } else {
                    R.string.my_attendance_history_loading
                },
            ),
        isLoading = !state.hasLoadMoreError,
        onRetry = if (state.hasLoadMoreError) onLoadMore else null,
    )
}
