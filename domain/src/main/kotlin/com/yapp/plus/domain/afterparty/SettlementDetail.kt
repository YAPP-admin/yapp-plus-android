package com.yapp.plus.domain.afterparty

data class SettlementDetail(
    val item: SettlementItem,
    val account: SettlementAccount,
    val totalAmount: Long,
    val perPersonAmount: Long,
    val debtorCount: Int,
    val paidCount: Int,
    val recipientIds: Set<Long>,
    val photos: List<SettlementPhoto>,
    val revision: Long,
)
