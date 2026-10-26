package com.yapp.plus.data.auth.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthTokensResponse(
    val accessToken: String,
    val refreshToken: String? = null,
)
