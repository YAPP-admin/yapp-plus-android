package com.yapp.plus.data.auth.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
internal data class UserRegistrationRequest(
    val registrationToken: String,
    val name: String,
    val phoneNumber: String,
)
