package com.yapp.plus.data.notification

interface NotificationDataSource {
    suspend fun fetchNotifications(cursor: String?): NotificationPageData
}
