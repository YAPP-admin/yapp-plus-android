package com.yapp.plus.feature.main.board

import androidx.compose.runtime.Composable
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
    BoardScreen(
        modifier = modifier,
        category = category,
        notices = NoticeUiState.Unavailable,
        detail = NoticeUiState.Unavailable.takeIf { noticeId != null },
        onCategorySelected = viewModel::selectCategory,
        onNoticeClick = onNoticeClick,
        onBack = onBack,
    )
}
