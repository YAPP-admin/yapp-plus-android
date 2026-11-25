package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class SettlementItemResponse(
    val settlementId: Long,
    val afterpartyId: Long,
    val creatorName: String,
    val isMe: Boolean,
    val participantCount: Int,
    val myAmount: Long,
    val paymentStatus: SettlementPaymentStatus?,
) {
    fun toDomain(): SettlementItem =
        SettlementItem(settlementId, afterpartyId, creatorName, isMe, participantCount, myAmount, paymentStatus)
}
