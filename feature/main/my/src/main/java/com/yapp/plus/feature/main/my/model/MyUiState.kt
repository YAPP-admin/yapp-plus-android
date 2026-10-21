package com.yapp.plus.feature.main.my.model

data class MyUiState(
    val submittedProfile: MyProfileUiState = MyProfileUiState.Loading,
    val testProfile: MyProfileSummaryState = MyProfileSummaryState.Loading,
    val attendanceHistory: MyAttendanceHistoryState = MyAttendanceHistoryState.NotRequested,
    val isAttendanceHistoryExpanded: Boolean = false,
)
