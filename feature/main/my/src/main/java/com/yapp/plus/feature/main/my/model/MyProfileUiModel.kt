package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.profile.UserProfile

data class MyProfileUiModel(
    val name: String,
    val role: MembershipStatus? = null,
    val generation: Int? = null,
    val position: UserPosition? = null,
) {
    internal companion object {
        fun from(profile: UserProfile): MyProfileUiModel =
            MyProfileUiModel(
                name = profile.name,
                role = profile.membership?.status,
                generation = profile.membership?.generationNumber,
                position = profile.membership?.position,
            )
    }
}
