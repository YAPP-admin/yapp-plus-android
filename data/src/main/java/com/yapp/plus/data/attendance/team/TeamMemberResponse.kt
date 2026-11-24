package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamMemberResponse(
    val membershipId: Long,
    val name: String,
    val position: String?,
    val isMe: Boolean,
) {
    fun toData(): TeamMemberData = TeamMemberData(membershipId, name, position)
}
