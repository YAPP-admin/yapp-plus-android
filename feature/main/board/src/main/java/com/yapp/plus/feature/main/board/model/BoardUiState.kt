package com.yapp.plus.feature.main.board.model

data class BoardUiState(
    val category: BoardCategory = BoardCategory.All,
    val notices: NoticeUiState<List<BoardNoticeUiModel>> = NoticeUiState.Loading,
    val detail: NoticeUiState<BoardNoticeUiModel>? = null,
    val nextCursor: String? = null,
    val isLoadingMore: Boolean = false,
    val hasLoadMoreError: Boolean = false,
    val isMarkingRead: Boolean = false,
    val hasReadError: Boolean = false,
)
