package com.yapp.plus.core.datastore

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.flow.first
import java.io.File
import java.security.MessageDigest

class SessionDataStore(
    context: Context,
    namespace: String,
) {
    private val applicationContext = context.applicationContext
    private val dataStore =
        DataStoreFactory.create(
            serializer = SessionTokenSerializer(),
            corruptionHandler = ReplaceFileCorruptionHandler { SessionTokenRecord() },
            produceFile = { sessionFile(applicationContext, namespace) },
        )

    suspend fun fetchTokens(): SessionTokenRecord = dataStore.data.first()

    suspend fun updateTokens(tokens: SessionTokenRecord) {
        dataStore.updateData { tokens }
    }

    private fun sessionFile(
        context: Context,
        namespace: String,
    ): File {
        val identifier =
            MessageDigest
                .getInstance("SHA-256")
                .digest(namespace.encodeToByteArray())
                .joinToString("") { "%02x".format(it) }
        return File(context.noBackupFilesDir, "sessions/$identifier.tokens")
    }
}
