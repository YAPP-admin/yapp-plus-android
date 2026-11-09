package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserPosition

data class MyProfileUiModel(
    val name: String,
    val role: MembershipStatus? = null,
    val generation: Int? = null,
    val position: UserPosition? = null,
)
