package com.yapp.plus.feature.main.my.model

sealed interface MyProfileSummaryState {
    data object Loading : MyProfileSummaryState

    data object Empty : MyProfileSummaryState

    data object Error : MyProfileSummaryState

    @JvmInline
    value class Content(
        val displayName: String,
    ) : MyProfileSummaryState
}

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

data class MyAttendanceHistoryItem(
    val sessionName: String,
    val checkedInAtLabel: String,
)

data class MyUiState(
    val submittedProfile: MyProfileUiState = MyProfileUiState.Loading,
    val testProfile: MyProfileSummaryState = MyProfileSummaryState.Loading,
    val attendanceHistory: MyAttendanceHistoryState = MyAttendanceHistoryState.NotRequested,
    val isAttendanceHistoryExpanded: Boolean = false,
)
