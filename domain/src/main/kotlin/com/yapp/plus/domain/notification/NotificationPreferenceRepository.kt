package com.yapp.plus.domain.notification

interface NotificationPreferenceRepository {
    suspend fun fetchEnabled(): Boolean

    suspend fun updateEnabled(enabled: Boolean)
}
