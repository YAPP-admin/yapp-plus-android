package com.yapp.plus.domain.auth

fun interface UserProfileRepository {
    suspend fun fetchName(): String?
}
