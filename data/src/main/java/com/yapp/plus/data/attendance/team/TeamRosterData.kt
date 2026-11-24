package com.yapp.plus.data.attendance.team

import com.yapp.plus.domain.attendance.TeamAttendanceRoster

data class TeamRosterData(
    val teamId: Long,
    val teamName: String,
    val selfMembershipId: Long,
    val members: List<TeamMemberData>,
) {
    fun toDomain(): TeamAttendanceRoster {
        require(teamId > 0 && teamName.isNotBlank())
        require(members.map { it.membershipId }.toSet().size == members.size)
        require(members.any { it.membershipId == selfMembershipId })
        return TeamAttendanceRoster(
            teamId = teamId,
            teamName = teamName,
            selfMembershipId = selfMembershipId,
            members = members.map(TeamMemberData::toDomain),
        )
    }
}
