package com.yapp.plus.data.notification

import android.content.Context
import com.yapp.plus.core.datastore.NotificationPreferenceDataStore
import java.io.File

class NotificationPreferenceLocalDataSource(
    context: Context,
) : NotificationPreferenceDataSource {
    private val store =
        NotificationPreferenceDataStore(
            File(context.applicationContext.noBackupFilesDir, "notification-preference"),
        )

    override suspend fun fetchEnabled(): Boolean = store.fetchEnabled()

    override suspend fun updateEnabled(enabled: Boolean) = store.updateEnabled(enabled)
}
