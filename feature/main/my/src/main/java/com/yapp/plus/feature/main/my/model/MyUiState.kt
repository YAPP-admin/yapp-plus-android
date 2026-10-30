package com.yapp.plus.feature.main.my.model

data class MyUiState(
    val profile: MyProfileUiState = MyProfileUiState.Unavailable,
    val score: MyAttendanceScoreState = MyAttendanceScoreState.Loading,
    val attendanceHistory: MyAttendanceHistoryState = MyAttendanceHistoryState.NotRequested,
)
