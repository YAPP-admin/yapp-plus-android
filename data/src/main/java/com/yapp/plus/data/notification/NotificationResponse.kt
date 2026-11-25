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
    val afterpartyId: Long? = null,
    val settlementId: Long? = null,
) {
    fun toData(): NotificationData =
        NotificationData(
            id = id,
            content = content,
            sentDate = sentDate,
            type = type,
            postId = postId,
            sessionId = sessionId,
            navigationTarget = navigationTarget,
            afterpartyId = afterpartyId,
            settlementId = settlementId,
        )
}
