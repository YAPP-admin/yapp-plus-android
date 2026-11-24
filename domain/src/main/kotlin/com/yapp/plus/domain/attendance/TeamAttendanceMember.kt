package com.yapp.plus.domain.attendance

import com.yapp.plus.domain.profile.UserPosition

data class TeamAttendanceMember(
    val membershipId: Long,
    val name: String,
    val position: UserPosition?,
)
