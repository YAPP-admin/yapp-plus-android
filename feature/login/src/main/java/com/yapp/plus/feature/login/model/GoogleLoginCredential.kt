package com.yapp.plus.feature.login.model

class GoogleLoginCredential internal constructor(
    val idToken: String,
    val nonce: String,
) {
    override fun toString(): String = "GoogleLoginCredential(idToken=<redacted>, nonce=<redacted>)"
}
