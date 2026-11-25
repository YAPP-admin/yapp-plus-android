package com.yapp.plus.domain.afterparty

data class SettlementSummary(
    val expectedReceivableAmount: Long,
    val perPersonAmount: Long,
    val debtorCount: Int,
    val paidCount: Int,
) {
    val remainingAmount: Long
        get() = expectedReceivableAmount - perPersonAmount * paidCount
}
