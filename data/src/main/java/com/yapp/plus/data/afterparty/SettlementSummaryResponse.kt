package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementSummary
import kotlinx.serialization.Serializable

@Serializable
internal data class SettlementSummaryResponse(
    val expectedReceivableAmount: Long,
    val perPersonAmount: Long,
    val debtorCount: Int,
    val paidCount: Int,
) {
    fun toDomain(): SettlementSummary =
        SettlementSummary(expectedReceivableAmount, perPersonAmount, debtorCount, paidCount)
}
