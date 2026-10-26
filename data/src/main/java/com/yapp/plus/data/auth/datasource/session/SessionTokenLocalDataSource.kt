package com.yapp.plus.data.auth.datasource.session

import android.content.Context
import com.yapp.plus.core.datastore.SessionDataStore
import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.Tokens

class SessionTokenLocalDataSource(
    context: Context,
    namespace: String,
) : TokenPersistence {
    private val dataStore = SessionDataStore(context, namespace)

    override suspend fun fetchTokens(): Tokens? {
        val record = dataStore.fetchTokens()
        val accessToken = record.accessToken ?: return null
        val refreshToken = checkNotNull(record.refreshToken)
        return Tokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    override suspend fun updateTokens(tokens: Tokens?) {
        val record =
            if (tokens == null) {
                SessionTokenRecord()
            } else {
                SessionTokenRecord(
                    accessToken = tokens.accessToken,
                    refreshToken = checkNotNull(tokens.refreshToken),
                )
            }
        dataStore.updateTokens(record)
    }
}
