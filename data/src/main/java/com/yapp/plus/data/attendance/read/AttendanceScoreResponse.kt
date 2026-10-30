package com.yapp.plus.data.attendance.read

import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceScoreResponse(
    val generationId: Long,
    val totalScore: Int,
    val attendanceCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyLeaveCount: Int,
) {
    fun toData(): AttendanceScoreData =
        AttendanceScoreData(
            generationId = generationId,
            totalScore = totalScore,
            attendanceCount = attendanceCount,
            lateCount = lateCount,
            absentCount = absentCount,
            earlyLeaveCount = earlyLeaveCount,
        )
}
