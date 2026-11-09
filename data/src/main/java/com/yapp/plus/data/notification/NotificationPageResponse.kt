package com.yapp.plus.data.notification

import kotlinx.serialization.Serializable

@Serializable
internal data class NotificationPageResponse(
    val items: List<NotificationResponse>,
    val nextCursor: String?,
) {
    fun toData(): NotificationPageData = NotificationPageData(items.map { it.toData() }, nextCursor)
}
