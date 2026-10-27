package com.yapp.plus.data.auth.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
internal data class GoogleNativeAuthenticationRequest(
    val idToken: String,
    val nonce: String,
)
