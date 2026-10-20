package com.yapp.plus.domain.auth

/** 인증 제공자의 토큰과 구분되는 앱의 인증 사용자 식별 정보다. */
data class AuthSession(
    val userId: String,
)
