package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementSummary
import java.math.BigInteger

internal data class MySettlementRow(
    val afterparty: AfterpartySummary,
    val settlementId: Long,
    val status: SettlementPaymentStatus?,
    val unpaidAmount: BigInteger? = null,
    val summary: SettlementSummary? = null,
)

internal fun AfterpartySettlements.listRows(isReceiving: Boolean): List<MySettlementRow> {
    if (isReceiving) {
        return items.filter { it.isCreator }.map { item ->
            val summary = ownerSummaries[item.id]
            val status =
                when {
                    summary == null -> null
                    summary.paidCount >= summary.debtorCount -> SettlementPaymentStatus.PAID
                    else -> SettlementPaymentStatus.UNPAID
                }
            MySettlementRow(
                afterparty = afterparty,
                settlementId = item.id,
                status = status,
                summary = summary,
            )
        }
    }

    val payable = items.filter { !it.isCreator && it.paymentStatus != null }
    val first =
        payable.firstOrNull { it.paymentStatus == SettlementPaymentStatus.UNPAID }
            ?: payable.firstOrNull()
            ?: return emptyList()
    return listOf(MySettlementRow(afterparty, first.id, first.paymentStatus, payable.unpaidAmount()))
}
