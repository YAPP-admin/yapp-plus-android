package com.yapp.plus.notification

import com.yapp.plus.domain.notification.NotificationPreferenceRepository

internal class FakeNotificationPreferences(
    var enabled: Boolean = true,
) : NotificationPreferenceRepository {
    var read: suspend () -> Boolean = { enabled }
    var write: suspend (Boolean) -> Unit = { enabled = it }

    override suspend fun fetchEnabled(): Boolean = read()

    override suspend fun updateEnabled(enabled: Boolean) = write(enabled)
}
