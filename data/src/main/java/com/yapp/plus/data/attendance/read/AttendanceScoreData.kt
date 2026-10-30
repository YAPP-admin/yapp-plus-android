package com.yapp.plus.data.attendance.read

data class AttendanceScoreData(
    val generationId: Long,
    val totalScore: Int,
    val attendanceCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyLeaveCount: Int,
)
