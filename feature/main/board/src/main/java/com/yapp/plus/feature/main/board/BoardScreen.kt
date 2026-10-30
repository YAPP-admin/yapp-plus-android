package com.yapp.plus.feature.main.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.main.YappDetailHeader
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.components.BoardCategorySelector
import com.yapp.plus.feature.main.board.components.BoardNoticeCard
import com.yapp.plus.feature.main.board.components.BoardNoticeDetail
import com.yapp.plus.feature.main.board.components.BoardNoticeState
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    category: BoardCategory = BoardCategory.All,
    notices: NoticeUiState<List<BoardNoticeUiModel>> = NoticeUiState.Unavailable,
    detail: NoticeUiState<BoardNoticeUiModel>? = null,
    onCategorySelected: (BoardCategory) -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
    nextCursor: String? = null,
    isLoadingMore: Boolean = false,
    hasLoadMoreError: Boolean = false,
    onLoadMore: () -> Unit = {},
    isMarkingRead: Boolean = false,
    hasReadError: Boolean = false,
    onMarkRead: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    LaunchedEffect(category) { listState.scrollToItem(0) }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        if (detail == null) {
            YappMainHeader(title = stringResource(R.string.board_title))
        } else {
            val category = (detail as? NoticeUiState.Content)?.value?.category
            YappDetailHeader(
                title = stringResource(category?.labelResource ?: R.string.board_title),
                backDescription = stringResource(R.string.board_back),
                onBack = onBack,
            )
        }
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
        ) {
            if (detail != null) {
                BoardNoticeState(detail, onRetry, R.string.board_missing, Modifier.weight(1f)) {
                    key(it.id) {
                        BoardNoticeDetail(it, onMarkRead, isMarkingRead, hasReadError, onRetry)
                    }
                }
            } else {
                BoardCategorySelector(category, onCategorySelected)
                BoardNoticeState(notices, onRetry, R.string.board_empty, Modifier.weight(1f)) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(it, key = { notice -> notice.id }) { notice ->
                            BoardNoticeCard(notice, { onNoticeClick(notice.id) })
                        }
                        if (nextCursor != null) {
                            item {
                                LaunchedEffect(nextCursor, hasLoadMoreError) {
                                    if (!hasLoadMoreError) onLoadMore()
                                }
                                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                                    if (hasLoadMoreError) {
                                        YappButton(stringResource(R.string.board_retry), onLoadMore)
                                    } else if (isLoadingMore) {
                                        CircularProgressIndicator(color = YappMainColor.brand)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
