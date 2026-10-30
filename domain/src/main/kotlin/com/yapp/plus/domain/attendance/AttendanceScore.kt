package com.yapp.plus.domain.attendance

data class AttendanceScore(
    val generationId: Long,
    val totalScore: Int,
    val attendanceCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyLeaveCount: Int,
)
