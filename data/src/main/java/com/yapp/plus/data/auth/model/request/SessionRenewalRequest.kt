package com.yapp.plus.data.auth.model.request

import kotlinx.serialization.Serializable

@Serializable
internal data class SessionRenewalRequest(
    val refreshToken: String,
)
