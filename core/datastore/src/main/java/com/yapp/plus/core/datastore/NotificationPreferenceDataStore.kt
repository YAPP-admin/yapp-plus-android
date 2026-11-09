package com.yapp.plus.core.datastore

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import java.io.File

class NotificationPreferenceDataStore(
    file: File,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) {
    private val store =
        DataStoreFactory.create(
            serializer = NotificationPreferenceSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { false },
            scope = scope,
            produceFile = { file },
        )

    suspend fun fetchEnabled(): Boolean = store.data.first()

    suspend fun updateEnabled(enabled: Boolean) {
        store.updateData { enabled }
    }
}
