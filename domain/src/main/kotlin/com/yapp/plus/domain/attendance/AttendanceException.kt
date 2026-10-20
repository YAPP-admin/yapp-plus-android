package com.yapp.plus.domain.attendance

class AttendanceException(
    val isInvalidCode: Boolean,
) : Exception("Attendance request failed")
