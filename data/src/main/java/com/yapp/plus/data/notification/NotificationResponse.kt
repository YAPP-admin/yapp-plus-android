package com.yapp.plus.data.notification

import kotlinx.serialization.Serializable

@Serializable
internal data class NotificationResponse(
    val id: Long,
    val content: String,
    val sentDate: String,
    val type: String,
    val postId: Long?,
    val sessionId: Long? = null,
    val navigationTarget: String? = null,
) {
    fun toData(): NotificationData = NotificationData(id, content, sentDate, type, postId, sessionId, navigationTarget)
}
