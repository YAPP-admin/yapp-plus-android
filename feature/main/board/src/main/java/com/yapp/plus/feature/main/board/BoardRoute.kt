package com.yapp.plus.feature.main.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Composable
fun BoardRoute(
    noticeId: Long?,
    onNoticeClick: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: BoardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(noticeId) { viewModel.showNotice(noticeId) }
    BoardScreen(
        modifier = modifier,
        category = state.category,
        notices = state.notices,
        detail = if (noticeId == null) null else state.detail ?: NoticeUiState.Loading,
        onCategorySelected = viewModel::selectCategory,
        onNoticeClick = onNoticeClick,
        onBack = onBack,
        onRetry = viewModel::retry,
        nextCursor = state.nextCursor,
        isLoadingMore = state.isLoadingMore,
        hasLoadMoreError = state.hasLoadMoreError,
        onLoadMore = viewModel::fetchNextPage,
        isMarkingRead = state.isMarkingRead,
        hasReadError = state.hasReadError,
        onMarkRead = viewModel::markRead,
    )
}
