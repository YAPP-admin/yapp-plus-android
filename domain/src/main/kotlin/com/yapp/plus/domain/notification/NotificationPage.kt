package com.yapp.plus.domain.notification

data class NotificationPage(
    val items: List<Notification>,
    val nextCursor: String?,
)
