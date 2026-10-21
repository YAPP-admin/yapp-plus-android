package com.yapp.plus.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseDeleteAccountResponse(
    val deleted: Boolean,
    @SerialName("user_id") val userId: String,
)
