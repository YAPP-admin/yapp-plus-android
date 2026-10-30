package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceHistoryEntry
import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceScore
import com.yapp.plus.domain.attendance.AttendanceStaffDecision

class AttendanceReadRepositoryImpl(
    private val dataSource: AttendanceReadDataSource,
) : AttendanceReadRepository {
    override suspend fun fetchScore(): AttendanceScore? = dataSource.fetchScore()?.toDomain()

    override suspend fun fetchHistory(cursor: String?): AttendanceHistoryPage =
        dataSource.fetchHistory(cursor).toDomain()
}

private fun AttendanceScoreData.toDomain(): AttendanceScore {
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

private fun AttendanceHistoryData.toDomain(): AttendanceHistoryPage {
    require(totalSessionCount >= 0 && startedSessionCount in 0..totalSessionCount)
    require(nextCursor == null || nextCursor.isNotBlank())
    return AttendanceHistoryPage(
        totalSessionCount = totalSessionCount,
        startedSessionCount = startedSessionCount,
        items = items.map { it.toDomain() },
        nextCursor = nextCursor,
    )
}

private fun AttendanceHistoryEntryData.toDomain(): AttendanceHistoryEntry {
    require(sessionId > 0 && sessionName.isNotBlank())
    return AttendanceHistoryEntry(
        sessionId = sessionId,
        sessionName = sessionName,
        status = AttendanceResultStatus.valueOf(attendanceStatus),
        staffDecision = staffDecisionStatus?.let(AttendanceStaffDecision::valueOf),
    )
}
