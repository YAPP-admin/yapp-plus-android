package com.yapp.plus.feature.main.home.notification.model

import com.yapp.plus.domain.notification.SettlementNotificationTarget

data class HomeNotificationUiModel(
    val id: Long,
    val message: String,
    val dateLabel: String,
    val postId: Long? = null,
    val sessionId: Long? = null,
    val settlement: SettlementNotificationTarget? = null,
)
