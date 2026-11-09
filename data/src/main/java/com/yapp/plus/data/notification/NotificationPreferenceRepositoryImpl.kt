package com.yapp.plus.data.notification

import com.yapp.plus.domain.notification.NotificationPreferenceRepository

class NotificationPreferenceRepositoryImpl(
    private val source: NotificationPreferenceDataSource,
) : NotificationPreferenceRepository {
    override suspend fun fetchEnabled(): Boolean = source.fetchEnabled()

    override suspend fun updateEnabled(enabled: Boolean) = source.updateEnabled(enabled)
}
