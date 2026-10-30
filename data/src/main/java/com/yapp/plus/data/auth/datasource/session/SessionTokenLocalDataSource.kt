package com.yapp.plus.data.auth.datasource.session

import android.content.Context
import com.yapp.plus.core.datastore.SessionDataStore
import com.yapp.plus.core.datastore.SessionRecordStore
import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.Tokens

class SessionTokenLocalDataSource(
    private val dataStore: SessionRecordStore,
) : TokenPersistence,
    SessionProfileDataSource {
    constructor(context: Context, namespace: String) : this(SessionDataStore(context, namespace))

    override suspend fun fetchTokens(): Tokens? {
        val record = dataStore.fetchTokens()
        val accessToken = record.accessToken ?: return null
        val refreshToken = checkNotNull(record.refreshToken)
        val restored = Tokens(accessToken = accessToken, refreshToken = refreshToken)
        return record.storageKey?.let { Tokens(accessToken, refreshToken, it) } ?: restored
    }

    override suspend fun updateTokens(tokens: Tokens?) {
        dataStore.updateSession { previous ->
            if (tokens == null) {
                SessionTokenRecord()
            } else {
                SessionTokenRecord(
                    accessToken = tokens.accessToken,
                    refreshToken = checkNotNull(tokens.refreshToken),
                    storageKey = tokens.storageKey,
                    userName =
                        previous.userName.takeIf {
                            previous.storageKey == tokens.storageKey
                        },
                )
            }
        }
    }

    override suspend fun fetchName(): String? =
        dataStore.fetchTokens().let { it.userName.takeIf { _ -> it.accessToken != null } }

    override suspend fun updateName(
        storageKey: String,
        name: String?,
    ): Boolean {
        val updated =
            dataStore.updateSession { current ->
                if (current.accessToken != null && current.storageKey == storageKey) {
                    current.copy(userName = name)
                } else {
                    current
                }
            }
        return updated.accessToken != null && updated.storageKey == storageKey
    }
}
