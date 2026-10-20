package com.yapp.plus.domain.auth

/** 로그인 교환에만 사용하며 ID 토큰과 원본 nonce를 로그나 영구 저장소에 남기지 않는다. */
class GoogleLoginCredential(
    val idToken: String,
    val rawNonce: String,
) {
    override fun toString(): String = "GoogleLoginCredential(idToken=<redacted>, rawNonce=<redacted>)"
}
