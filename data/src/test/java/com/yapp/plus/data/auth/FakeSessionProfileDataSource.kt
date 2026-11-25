package com.yapp.plus.data.auth

import com.yapp.plus.data.auth.datasource.session.SessionProfileDataSource

internal class FakeSessionProfileDataSource : SessionProfileDataSource {
    var name: String? = null
    var reviewStatus: String? = null

    override suspend fun fetchName(): String? = name

    override suspend fun updateName(
        storageKey: String,
        name: String?,
    ): Boolean {
        this.name = name
        return true
    }

    override suspend fun fetchReviewStatus(storageKey: String): String? = reviewStatus

    override suspend fun updateReviewStatus(
        storageKey: String,
        reviewStatus: String,
    ): Boolean {
        this.reviewStatus = reviewStatus
        return true
    }
}
