package com.yapp.plus.data.notification

import com.yapp.plus.domain.notification.NotificationPage
import com.yapp.plus.domain.notification.NotificationRepository

class NotificationRepositoryImpl(
    private val source: NotificationDataSource,
) : NotificationRepository {
    override suspend fun fetchNotifications(cursor: String?): NotificationPage =
        source.fetchNotifications(cursor).toDomain()
}
