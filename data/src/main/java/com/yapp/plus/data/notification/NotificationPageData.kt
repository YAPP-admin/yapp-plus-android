package com.yapp.plus.data.notification

import com.yapp.plus.domain.notification.NotificationPage

data class NotificationPageData(
    val items: List<NotificationData>,
    val nextCursor: String?,
) {
    internal fun toDomain(): NotificationPage {
        require(nextCursor == null || nextCursor.isNotBlank())
        return NotificationPage(items.map { it.toDomain() }, nextCursor)
    }
}
