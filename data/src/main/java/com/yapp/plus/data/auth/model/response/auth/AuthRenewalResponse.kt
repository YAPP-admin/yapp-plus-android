package com.yapp.plus.data.auth.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthRenewalResponse(
    val result: String,
    val session: AuthTokensResponse? = null,
)
