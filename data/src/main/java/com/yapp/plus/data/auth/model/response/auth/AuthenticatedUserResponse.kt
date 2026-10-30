package com.yapp.plus.data.auth.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthenticatedUserResponse(
    val name: String,
)
