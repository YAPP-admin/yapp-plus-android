package com.yapp.plus.data.auth.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiErrorResponse(
    val code: String,
    val message: String? = null,
)
