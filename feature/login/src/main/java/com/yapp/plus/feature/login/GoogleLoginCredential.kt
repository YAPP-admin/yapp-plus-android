package com.yapp.plus.feature.login

/** Google ID token and its matching nonce, kept in memory for server-side verification. */
class GoogleLoginCredential internal constructor(
    val idToken: String,
    val nonce: String,
) {
    override fun toString(): String = "GoogleLoginCredential(idToken=<redacted>, nonce=<redacted>)"
}
