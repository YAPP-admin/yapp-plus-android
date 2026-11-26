package com.yapp.plus.feature.main.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.toast.YappToastData
import com.yapp.plus.core.designsystem.components.toast.YappToastHost
import com.yapp.plus.core.designsystem.components.toast.YappToastManager
import com.yapp.plus.core.designsystem.components.toast.YappToastType
import com.yapp.plus.core.ui.ObserveAsEvents
import com.yapp.plus.feature.main.board.model.BoardEvent
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Composable
fun BoardRoute(
    toastManager: YappToastManager,
    noticeId: Long?,
    onNoticeClick: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: BoardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val readErrorMessage = stringResource(R.string.board_read_error)
    LaunchedEffect(noticeId) {
        viewModel.showNotice(noticeId)
        if (noticeId == null && viewModel.uiState.value.notices == NoticeUiState.Error) {
            viewModel.retry()
        }
    }
    Box(modifier.fillMaxSize()) {
        BoardScreen(
            category = state.category,
            notices = state.notices,
            detail = if (noticeId == null) null else state.detail ?: NoticeUiState.Loading,
            onCategorySelected = viewModel::selectCategory,
            onNoticeClick = onNoticeClick,
            onBack = onBack,
            nextCursor = state.nextCursor,
            isLoadingMore = state.isLoadingMore,
            hasLoadMoreError = state.hasLoadMoreError,
            onLoadMore = viewModel::fetchNextPage,
            isMarkingRead = state.isMarkingRead,
            onMarkRead = viewModel::markRead,
        )
        YappToastHost(toastManager = toastManager)
    }
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BoardEvent.ReadFailed ->
                if (event.noticeId == noticeId) {
                    toastManager.tryShow(
                        YappToastData(text = readErrorMessage, type = YappToastType.Error),
                    )
                }
        }
    }
}
