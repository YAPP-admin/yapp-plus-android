package com.yapp.plus.data.attendance.team

import com.yapp.plus.domain.attendance.TeamAttendanceMember
import com.yapp.plus.domain.profile.UserPosition

data class TeamMemberData(
    val membershipId: Long,
    val name: String,
    val position: String?,
) {
    fun toDomain(): TeamAttendanceMember {
        require(membershipId > 0 && name.isNotBlank())
        val userPosition =
            position?.let { value ->
                UserPosition.entries.firstOrNull { it.name == value } ?: UserPosition.UNKNOWN
            }
        return TeamAttendanceMember(membershipId, name, userPosition)
    }
}
