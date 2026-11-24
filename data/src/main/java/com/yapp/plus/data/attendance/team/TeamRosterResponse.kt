package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamRosterResponse(
    val teamId: Long,
    val teamName: String,
    val members: List<TeamMemberResponse>,
) {
    fun toData(): TeamRosterData =
        TeamRosterData(
            teamId = teamId,
            teamName = teamName,
            selfMembershipId = members.single { it.isMe }.membershipId,
            members = members.map(TeamMemberResponse::toData),
        )
}
