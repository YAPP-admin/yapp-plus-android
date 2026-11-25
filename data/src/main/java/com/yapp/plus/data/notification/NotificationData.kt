package com.yapp.plus.data.notification

import com.yapp.plus.domain.notification.Notification
import com.yapp.plus.domain.notification.SettlementNotificationTarget
import java.time.LocalDate

data class NotificationData(
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
    internal fun toDomain(): Notification {
        require(id > 0 && content.isNotBlank())
        when (type) {
            "GENERAL", "SESSION_REMINDER", "SETTLEMENT" -> require(postId == null)
            "POST" -> require(postId != null && postId > 0)
            else -> error("Unknown notification type")
        }
        val scheduleId =
            sessionId?.takeIf { it > 0 && type == "SESSION_REMINDER" && navigationTarget == "SESSION_SCHEDULE" }
        val target =
            if (type == "SETTLEMENT" &&
                navigationTarget == "SETTLEMENT" &&
                afterpartyId != null &&
                afterpartyId > 0 &&
                settlementId != null &&
                settlementId > 0
            ) {
                SettlementNotificationTarget(afterpartyId, settlementId)
            } else {
                null
            }
        return Notification(id, content, LocalDate.parse(sentDate), postId, sessionId = scheduleId, settlement = target)
    }
}
