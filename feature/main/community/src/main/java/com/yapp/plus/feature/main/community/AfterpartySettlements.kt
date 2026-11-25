package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.afterparty.SettlementSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.math.BigInteger

internal data class AfterpartySettlements(
    val afterparty: AfterpartySummary,
    val items: List<SettlementItem>,
    val ownerSummaries: Map<Long, SettlementSummary>,
)

internal suspend fun SettlementRepository.fetchOwnerSummaries(
    afterpartyId: Long,
    items: List<SettlementItem>,
): Map<Long, SettlementSummary> =
    buildMap {
        for (owners in items.filter { it.isCreator }.chunked(4)) {
            val results =
                coroutineScope {
                    owners
                        .map { item ->
                            async { item.id to fetchSummary(afterpartyId, item.id).valueOrNull() }
                        }.awaitAll()
                }
            for ((id, summary) in results) {
                if (summary != null) put(id, summary)
            }
        }
    }

internal fun Iterable<SettlementItem>.unpaidAmount(): BigInteger =
    fold(BigInteger.ZERO) { total, item ->
        if (item.paymentStatus == SettlementPaymentStatus.UNPAID) {
            total + BigInteger.valueOf(item.myAmount)
        } else {
            total
        }
    }
