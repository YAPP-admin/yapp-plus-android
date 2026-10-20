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
    val category by viewModel.category.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val detail by viewModel.detail.collectAsStateWithLifecycle()
    val displayedDetail = detail.takeIf { noticeId == viewModel.noticeId } ?: NoticeUiState.Loading
    LaunchedEffect(noticeId) { viewModel.showNotice(noticeId) }
    BoardScreen(
        modifier = modifier,
        category = category,
        notices = notices,
        detail = displayedDetail.takeIf { noticeId != null },
        onCategorySelected = viewModel::selectCategory,
        onNoticeClick = onNoticeClick,
        onRetry = if (noticeId == null) viewModel::retry else viewModel::retryDetail,
        onBack = onBack,
    )
}
