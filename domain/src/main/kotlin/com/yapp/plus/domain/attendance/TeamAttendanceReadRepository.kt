package com.yapp.plus.domain.attendance

interface TeamAttendanceReadRepository {
    suspend fun fetchTeamRoster(sessionId: Long): TeamAttendanceRoster

    suspend fun fetchLatestPhoto(sessionId: Long): TeamAttendancePhoto?
}
