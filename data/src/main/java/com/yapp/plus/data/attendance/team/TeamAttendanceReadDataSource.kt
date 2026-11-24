package com.yapp.plus.data.attendance.team

interface TeamAttendanceReadDataSource {
    suspend fun fetchTeamRoster(sessionId: Long): TeamRosterData

    suspend fun fetchLatestPhoto(sessionId: Long): TeamPhotoDetailsData?
}
