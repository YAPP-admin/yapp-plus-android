package com.yapp.plus.data.auth

import com.yapp.plus.data.auth.datasource.session.SessionProfileDataSource

internal class FakeSessionProfileDataSource : SessionProfileDataSource {
    var name: String? = null

    override suspend fun fetchName(): String? = name

    override suspend fun updateName(
        storageKey: String,
        name: String?,
    ): Boolean {
        this.name = name
        return true
    }
}
