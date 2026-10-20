package com.yapp.plus.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class SupabaseTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: SupabaseUserResponse,
)
