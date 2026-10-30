package com.yapp.plus.feature.main.my.model

data class MyAttendanceHistoryItem(
    val sessionId: Long,
    val sessionName: String,
    val status: MyAttendanceStatus,
)
