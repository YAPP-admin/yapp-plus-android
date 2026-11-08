package com.yapp.plus.domain.notification

interface NotificationRepository {
    suspend fun fetchNotifications(cursor: String? = null): NotificationPage
}
