package com.yapp.plus.domain.my

import java.time.Instant

data class MyAttendanceRecord(
    val sessionId: Long,
    val sessionName: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val checkedInAt: Instant,
)
