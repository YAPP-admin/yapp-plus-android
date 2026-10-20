package com.yapp.plus.data.auth

import kotlinx.serialization.Serializable

@Serializable
internal class SessionRenewalRequest(
    val refreshToken: String,
)
