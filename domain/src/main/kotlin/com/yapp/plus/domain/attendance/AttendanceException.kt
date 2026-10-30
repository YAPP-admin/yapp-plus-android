package com.yapp.plus.domain.attendance

class AttendanceException(
    val isInvalidCode: Boolean = false,
    val code: String? = null,
) : Exception("Attendance request failed")
