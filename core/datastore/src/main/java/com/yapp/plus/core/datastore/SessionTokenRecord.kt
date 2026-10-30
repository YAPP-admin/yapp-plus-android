package com.yapp.plus.core.datastore

import kotlinx.serialization.Serializable

@Serializable
data class SessionTokenRecord(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val storageKey: String? = null,
    val userName: String? = null,
) {
    init {
        require(
            (accessToken == null && refreshToken == null) ||
                (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()),
        )
    }

    override fun toString(): String = "SessionTokenRecord(***)"
}
