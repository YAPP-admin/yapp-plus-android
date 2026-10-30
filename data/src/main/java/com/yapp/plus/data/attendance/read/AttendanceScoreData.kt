package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceScore

data class AttendanceScoreData(
    val generationId: Long,
    val totalScore: Int,
    val attendanceCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val earlyLeaveCount: Int,
) {
    internal fun toDomain(): AttendanceScore {
        require(generationId > 0)
        require(
            listOf(
                totalScore,
                attendanceCount,
                lateCount,
                absentCount,
                earlyLeaveCount,
            ).all { it >= 0 },
        )
        return AttendanceScore(
            generationId = generationId,
            totalScore = totalScore,
            attendanceCount = attendanceCount,
            lateCount = lateCount,
            absentCount = absentCount,
            earlyLeaveCount = earlyLeaveCount,
        )
    }
}
