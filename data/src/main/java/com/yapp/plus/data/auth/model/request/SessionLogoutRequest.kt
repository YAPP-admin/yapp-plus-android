package com.yapp.plus.data.auth.model.request

import kotlinx.serialization.Serializable

@Serializable
internal data class SessionLogoutRequest(
    val refreshToken: String,
) {
    override fun toString(): String = "SessionLogoutRequest(refreshToken=<redacted>)"
}
