package com.yapp.plus.data.notification

import kotlinx.serialization.Serializable

@Serializable
internal data class NotificationResponse(
    val id: Long,
    val content: String,
    val sentDate: String,
    val type: String,
    val postId: Long?,
) {
    fun toData(): NotificationData = NotificationData(id, content, sentDate, type, postId)
}
