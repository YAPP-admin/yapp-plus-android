package com.yapp.plus.data.my

import java.time.Instant

data class MyAttendanceRecordData(
    val sessionId: Long,
    val sessionName: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val checkedInAt: Instant,
)
