package com.yapp.plus.data.attendance.team

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TeamAttendanceReadRemoteDataSource(
    private val client: HttpClient,
) : TeamAttendanceReadDataSource {
    override suspend fun fetchTeamRoster(sessionId: Long): TeamRosterData =
        client.get("sessions/$sessionId/team-members").body<TeamRosterResponse>().toData()

    override suspend fun fetchLatestPhoto(sessionId: Long): TeamPhotoDetailsData? =
        client
            .get("sessions/$sessionId/team-attendance-photos")
            .body<TeamPhotoLookupResponse>()
            .photo
            ?.toData()
}
