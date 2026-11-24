package com.yapp.plus.data.attendance.team

import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRoster

class TeamAttendanceReadRepositoryImpl(
    private val dataSource: TeamAttendanceReadDataSource,
) : TeamAttendanceReadRepository {
    override suspend fun fetchTeamRoster(sessionId: Long): TeamAttendanceRoster =
        dataSource.fetchTeamRoster(sessionId).toDomain()

    override suspend fun fetchLatestPhoto(sessionId: Long): TeamAttendancePhoto? =
        dataSource.fetchLatestPhoto(sessionId)?.toDomain()
}
