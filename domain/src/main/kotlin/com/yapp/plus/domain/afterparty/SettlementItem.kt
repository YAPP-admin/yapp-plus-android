package com.yapp.plus.domain.afterparty

data class SettlementItem(
    val id: Long,
    val afterpartyId: Long,
    val creatorName: String,
    val isCreator: Boolean,
    val participantCount: Int,
    val myAmount: Long,
    val paymentStatus: SettlementPaymentStatus?,
)
