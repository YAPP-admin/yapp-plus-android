package com.yapp.plus.data.auth

import kotlinx.serialization.Serializable

@Serializable
internal class SessionRenewalResponse(
    val result: String,
    val session: SessionResponse? = null,
)
