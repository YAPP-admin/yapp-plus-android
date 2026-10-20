package com.yapp.plus.data.auth

import kotlinx.serialization.Serializable

@Serializable
internal class SessionResponse(
    val accessToken: String,
    val refreshToken: String? = null,
)
