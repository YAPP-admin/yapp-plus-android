package com.yapp.plus.feature.main.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappDetailHeader
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.toast.YappToastData
import com.yapp.plus.core.designsystem.components.toast.YappToastHost
import com.yapp.plus.core.designsystem.components.toast.YappToastManager
import com.yapp.plus.core.designsystem.components.toast.YappToastType
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.components.BoardCategorySelector
import com.yapp.plus.feature.main.board.components.BoardNoticeCard
import com.yapp.plus.feature.main.board.components.BoardNoticeDetail
import com.yapp.plus.feature.main.board.components.BoardNoticeDetailError
import com.yapp.plus.feature.main.board.components.BoardNoticeError
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
    val isDetailError = detail == NoticeUiState.Error
    val toastManager = remember { YappToastManager() }
    val readErrorMessage = stringResource(R.string.board_read_error)

    LaunchedEffect(category) { listState.scrollToItem(0) }
    LaunchedEffect(hasReadError) {
        if (hasReadError) {
            toastManager.tryShow(
                YappToastData(
                    text = readErrorMessage,
                    type = YappToastType.Error,
                ),
            )
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (detail == null) {
                YappMainHeader(title = stringResource(R.string.board_title))
            } else {
                val category = (detail as? NoticeUiState.Content)?.value?.category
                YappDetailHeader(
                    title =
                        if (isDetailError) {
                            ""
                        } else {
                            stringResource(category?.labelResource ?: R.string.board_title)
                        },
                    backDescription = stringResource(R.string.board_back),
                    onBack = onBack,
                    modifier = if (isDetailError) Modifier.height(51.dp) else Modifier,
                )
            }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .padding(top = if (isDetailError) 14.dp else 10.dp),
            ) {
                if (isDetailError) {
                    BoardNoticeDetailError(Modifier.fillMaxSize())
                } else if (detail != null) {
                    BoardNoticeState(detail, R.string.board_missing, Modifier.weight(1f)) {
                        key(it.id) {
                            BoardNoticeDetail(it, onMarkRead, isMarkingRead)
                        }
                    }
                } else {
                    BoardCategorySelector(category, onCategorySelected)
                    BoardNoticeState(notices, R.string.board_empty, Modifier.weight(1f)) {
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
                                            BoardNoticeError()
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
        YappToastHost(toastManager = toastManager)
    }
}
