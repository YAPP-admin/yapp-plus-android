package com.yapp.plus.data.auth

import android.content.Context
import com.yapp.plus.core.datastore.SessionDataStore
import com.yapp.plus.core.datastore.SessionTokenRecord

class SupabaseTokenLocalDataSource(
    context: Context,
    projectUrl: String,
) : AuthTokenLocalDataSource {
    private val dataStore = SessionDataStore(context, "supabase-auth:${projectUrl.trimEnd('/')}")

    override suspend fun fetchTokens(): SessionTokenRecord = dataStore.fetchTokens()

    override suspend fun updateTokens(tokens: SessionTokenRecord) = dataStore.updateTokens(tokens)
}
