package com.yapp.plus.feature.main.my.model

sealed interface MyAttendanceHistoryState {
    data object NotRequested : MyAttendanceHistoryState

    data object Loading : MyAttendanceHistoryState

    data object Error : MyAttendanceHistoryState

    data class Content(
        val summary: MySessionSummary,
        val items: List<MyAttendanceHistoryItem>,
        val nextCursor: String? = null,
        val isLoadingMore: Boolean = false,
        val hasLoadMoreError: Boolean = false,
    ) : MyAttendanceHistoryState
}
