package com.yapp.plus.data.notification

interface NotificationPreferenceDataSource {
    suspend fun fetchEnabled(): Boolean

    suspend fun updateEnabled(enabled: Boolean)
}
