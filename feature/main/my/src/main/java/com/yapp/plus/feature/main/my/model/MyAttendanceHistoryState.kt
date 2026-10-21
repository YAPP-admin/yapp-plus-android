package com.yapp.plus.feature.main.my.model

sealed interface MyAttendanceHistoryState {
    data object NotRequested : MyAttendanceHistoryState

    data object Loading : MyAttendanceHistoryState

    data object Empty : MyAttendanceHistoryState

    data object Error : MyAttendanceHistoryState

    @JvmInline
    value class Content(
        val items: List<MyAttendanceHistoryItem>,
    ) : MyAttendanceHistoryState
}
