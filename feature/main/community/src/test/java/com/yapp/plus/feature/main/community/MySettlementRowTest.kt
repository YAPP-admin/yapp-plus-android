package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigInteger
import java.time.Instant

class MySettlementRowTest {
    private val party =
        AfterpartySummary(
            id = 9,
            title = "뒤풀이",
            schedule = AfterpartySchedule(Instant.EPOCH, "", Instant.EPOCH, ""),
            state = AfterpartyState.ENDED,
            respondedCount = 0,
        )

    @Test
    fun payingRowsGroupUnpaidAmountsAndLinkToTheFirstUnpaidSettlement() {
        val paid = item(1, 34_000, SettlementPaymentStatus.PAID)
        val unpaid = item(2, 16_000, SettlementPaymentStatus.UNPAID)
        val another = item(3, 20_000, SettlementPaymentStatus.UNPAID)

        val row =
            AfterpartySettlements(party, listOf(paid, unpaid, another), emptyMap())
                .listRows(isReceiving = false)
                .single()

        assertEquals(2L, row.settlementId)
        assertEquals(SettlementPaymentStatus.UNPAID, row.status)
        assertEquals(BigInteger.valueOf(36_000), row.unpaidAmount)
    }

    @Test
    fun allPaidSettlementsShowZeroRemaining() {
        val paid = item(8, 34_000, SettlementPaymentStatus.PAID)
        val row = AfterpartySettlements(party, listOf(paid), emptyMap()).listRows(isReceiving = false).single()

        assertEquals(SettlementPaymentStatus.PAID, row.status)
        assertEquals(BigInteger.ZERO, row.unpaidAmount)
    }

    @Test
    fun receivingRowsUseServerProgressAndLeaveUnknownProgressUndetermined() {
        val active = item(1, 0, null).copy(isCreator = true)
        val complete = item(2, 0, null).copy(isCreator = true)
        val unknown = item(3, 0, null).copy(isCreator = true)
        val summaries =
            mapOf(
                1L to SettlementSummary(80_000, 20_000, 4, 2),
                2L to SettlementSummary(20_000, 20_000, 1, 1),
            )

        val rows =
            AfterpartySettlements(
                party,
                listOf(active, complete, unknown),
                summaries,
            ).listRows(isReceiving = true)

        assertEquals(SettlementPaymentStatus.UNPAID, rows[0].status)
        assertEquals(summaries[1L], rows[0].summary)
        assertEquals(SettlementPaymentStatus.PAID, rows[1].status)
        assertNull(rows[2].status)
        assertNull(rows[2].summary)
    }

    private fun item(
        id: Long,
        amount: Long,
        status: SettlementPaymentStatus?,
    ): SettlementItem = SettlementItem(id, party.id, "정산자", false, 3, amount, status)
}
