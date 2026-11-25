package com.yapp.plus.data.auth.datasource.session

interface SessionProfileDataSource {
    suspend fun fetchName(): String?

    suspend fun updateName(
        storageKey: String,
        name: String?,
    ): Boolean

    suspend fun fetchReviewStatus(storageKey: String): String?

    suspend fun updateReviewStatus(
        storageKey: String,
        reviewStatus: String,
    ): Boolean
}
