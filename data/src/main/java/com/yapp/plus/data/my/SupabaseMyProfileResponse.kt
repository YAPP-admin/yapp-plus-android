package com.yapp.plus.data.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseMyProfileResponse(
    @SerialName("display_name") val displayName: String,
)
