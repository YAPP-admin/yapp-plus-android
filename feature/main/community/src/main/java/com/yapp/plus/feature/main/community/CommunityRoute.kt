package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.feature.main.community.CommunityListState.LoadingIndicator
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilters
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityTitle
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun CommunityRoute(onAfterpartyClick: (Long) -> Unit) {
    val viewModel: CommunityViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    CommunityList(
        state = state,
        onFilter = viewModel::refresh,
        onRefresh = viewModel::pullToRefresh,
        onMore = viewModel::loadMore,
        onAfterpartyClick = onAfterpartyClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommunityList(
    state: CommunityListState,
    onFilter: (AfterpartyFilter) -> Unit,
    onRefresh: () -> Unit,
    onMore: () -> Unit,
    onAfterpartyClick: (Long) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = state.loading == LoadingIndicator.PullToRefresh
    val isListLoading = state.loading == LoadingIndicator.List

    Column(Modifier.fillMaxSize()) {
        YappMainHeader(title = stringResource(R.string.community_title))
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CommunityTitle(stringResource(R.string.community_afterparties))
            CommunityFilters(AfterpartyFilter.entries, state.filter, { stringResource(it.label()) }, onFilter)
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            indicator = {
                if (!isListLoading) {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }
            },
        ) {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                items(state.items, key = { it.id }) { item ->
                    CommunityAfterpartyRow(item, onClick = { onAfterpartyClick(item.id) })
                }
                item {
                    when {
                        isListLoading -> CommunityLoading()
                        state.hasError -> CommunityError()
                        state.loading == null && state.items.isEmpty() ->
                            CommunityCaption(
                                stringResource(R.string.community_empty),
                                Modifier.padding(vertical = 24.dp),
                            )
                    }
                    if (state.loading == null && !state.hasError && state.nextCursor != null) {
                        LaunchedEffect(state.nextCursor) { onMore() }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CommunityAfterpartyRow(
    item: AfterpartySummary,
    onClick: () -> Unit,
) {
    YappClickableSurface(onClick = onClick, color = YappMainColor.surface) {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            YappText(
                text = afterpartyStatusLabel(item),
                style = YappTypography.scaleT2Medium,
                color = if (item.state == AfterpartyState.OPEN) YappMainColor.brand else YappMainColor.textSubtle,
            )
            CommunityTitle(item.title)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CommunityCaption(item.schedule.startsAtDisplay)
                CommunityCaption(stringResource(R.string.community_responded, item.respondedCount))
            }
        }
    }
    HorizontalDivider(color = YappMainColor.divider)
}

@Composable
private fun afterpartyStatusLabel(item: AfterpartySummary): String {
    val today = Instant.now().atZone(korea).toLocalDate()
    val deadline =
        item.schedule.deadline
            .atZone(korea)
            .toLocalDate()
    return when {
        item.state == AfterpartyState.ENDED -> stringResource(R.string.community_ended)
        !Instant.now().isBefore(item.schedule.deadline) -> stringResource(R.string.community_closed)
        deadline == today -> stringResource(R.string.community_today)
        else -> stringResource(R.string.community_dday, ChronoUnit.DAYS.between(today, deadline))
    }
}

private fun AfterpartyFilter.label(): Int =
    when (this) {
        AfterpartyFilter.ALL -> R.string.community_all
        AfterpartyFilter.OPEN -> R.string.community_open
        AfterpartyFilter.CLOSED -> R.string.community_closed
    }

private val korea = ZoneId.of("Asia/Seoul")
