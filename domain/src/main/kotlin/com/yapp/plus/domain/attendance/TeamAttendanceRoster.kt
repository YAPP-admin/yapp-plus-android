package com.yapp.plus.domain.attendance

data class TeamAttendanceRoster(
    val teamId: Long,
    val teamName: String,
    val selfMembershipId: Long,
    val members: List<TeamAttendanceMember>,
)
