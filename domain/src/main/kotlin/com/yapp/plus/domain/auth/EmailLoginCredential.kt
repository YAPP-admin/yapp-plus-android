package com.yapp.plus.domain.auth

class EmailLoginCredential(
    val email: String,
    val password: String,
) {
    override fun toString(): String = "EmailLoginCredential([REDACTED])"
}
