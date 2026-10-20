package com.yapp.plus.feature.main.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.components.BoardCategorySelector
import com.yapp.plus.feature.main.board.components.BoardNoticeCard
import com.yapp.plus.feature.main.board.components.BoardNoticeDetail
import com.yapp.plus.feature.main.board.components.BoardNoticeState
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    category: BoardCategory = BoardCategory.All,
    notices: NoticeUiState<List<BoardNoticeUiModel>> = NoticeUiState.Empty,
    detail: NoticeUiState<BoardNoticeUiModel>? = null,
    onCategorySelected: (BoardCategory) -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        YappMainHeader(
            title = stringResource(R.string.board_title),
            iconResource = DesignSystemR.drawable.ic_chevron_left.takeIf { detail != null },
            iconDescription = stringResource(R.string.board_back),
            onIconClick = onBack,
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
        ) {
            if (detail != null) {
                BoardNoticeState(detail, onRetry, R.string.board_missing, Modifier.weight(1f)) {
                    key(it.id) { BoardNoticeDetail(it) }
                }
            } else {
                BoardCategorySelector(category, onCategorySelected)
                BoardNoticeState(notices, onRetry, R.string.board_empty, Modifier.weight(1f)) {
                    LazyColumn(state = listState) {
                        items(it, key = { notice -> notice.id }) { notice ->
                            BoardNoticeCard(notice, { onNoticeClick(notice.id) })
                        }
                    }
                }
            }
        }
    }
}
