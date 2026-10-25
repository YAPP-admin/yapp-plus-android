package com.yapp.plus.feature.main.my.model

data class MyUiState(
    val profile: MyProfileUiState = MyProfileUiState.Unavailable,
    val attendanceHistory: MyAttendanceHistoryState = MyAttendanceHistoryState.Unavailable,
    val isAttendanceHistoryExpanded: Boolean = false,
)
