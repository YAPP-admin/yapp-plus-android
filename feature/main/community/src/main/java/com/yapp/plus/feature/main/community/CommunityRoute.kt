package com.yapp.plus.feature.main.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.refresh.YappPullToRefreshBox
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.feature.main.community.CommunityListState.LoadingIndicator
import com.yapp.plus.feature.main.community.components.CommunityAfterpartyRow
import com.yapp.plus.feature.main.community.components.CommunityEmptyState
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityGatherings
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunitySection
import com.yapp.plus.feature.main.community.components.CommunityTabs
import com.yapp.plus.feature.main.community.components.CommunityVoteFilters
import java.time.Instant

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

@Composable
internal fun CommunityList(
    state: CommunityListState,
    onFilter: (AfterpartyFilter) -> Unit,
    onRefresh: () -> Unit,
    onMore: () -> Unit,
    onAfterpartyClick: (Long) -> Unit,
    now: Instant = Instant.now(),
) {
    var section by rememberSaveable { mutableStateOf(CommunitySection.Afterparties) }
    val sectionState = rememberSaveableStateHolder()

    Column(
        Modifier
            .fillMaxSize()
            .background(YappMainColor.surface),
    ) {
        YappMainHeader(title = stringResource(R.string.community_title))
        CommunityTabs(selectedSection = section, onSelect = { section = it })
        sectionState.SaveableStateProvider(section.name) {
            when (section) {
                CommunitySection.Afterparties ->
                    CommunityAfterpartyList(
                        state = state,
                        onFilter = onFilter,
                        onRefresh = onRefresh,
                        onMore = onMore,
                        onAfterpartyClick = onAfterpartyClick,
                        now = now,
                    )
                CommunitySection.Gatherings -> CommunityGatherings()
            }
        }
    }
}

@Composable
private fun CommunityAfterpartyList(
    state: CommunityListState,
    onFilter: (AfterpartyFilter) -> Unit,
    onRefresh: () -> Unit,
    onMore: () -> Unit,
    onAfterpartyClick: (Long) -> Unit,
    now: Instant,
) {
    val isRefreshing = state.loading == LoadingIndicator.PullToRefresh
    val isListLoading = state.loading == LoadingIndicator.List

    CommunityVoteFilters(selectedFilter = state.filter, onFilter = onFilter)
    YappPullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        showIndicator = !isListLoading,
    ) {
        if (state.loading == null && !state.hasError && state.items.isEmpty()) {
            val emptyMessage =
                when (state.filter) {
                    AfterpartyFilter.ALL, AfterpartyFilter.OPEN -> stringResource(R.string.community_empty_open)
                    AfterpartyFilter.CLOSED -> stringResource(R.string.community_empty_closed)
                }
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                CommunityEmptyState(text = emptyMessage)
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            ) {
                items(state.items, key = { it.id }) { item ->
                    CommunityAfterpartyRow(item, now) { onAfterpartyClick(item.id) }
                }
                item {
                    when {
                        isListLoading -> CommunityLoading(Modifier.fillMaxWidth().padding(24.dp))
                        state.hasError -> CommunityError()
                    }
                    if (state.loading == null && !state.hasError && state.nextCursor != null) {
                        LaunchedEffect(state.nextCursor) { onMore() }
                    }
                }
            }
        }
    }
}
