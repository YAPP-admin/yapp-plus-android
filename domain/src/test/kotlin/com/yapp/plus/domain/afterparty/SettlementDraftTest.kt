package com.yapp.plus.domain.afterparty

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettlementDraftTest {
    private val draft =
        SettlementDraft(
            account = SettlementAccount(SettlementBank.TOSS, "001-234", "김야뿌"),
            totalAmount = 100_001,
            perPersonAmount = 33_333,
            recipientIds = setOf(4, 5),
            imageIds = listOf(91, 92),
        )

    @Test
    fun acceptsLeadingZeroAndIndependentlyEnteredAmounts() {
        assertTrue(draft.isValid())
    }

    @Test
    fun rejectsOverflowAndRepeatedPhotoIds() {
        assertFalse(draft.copy(perPersonAmount = Long.MAX_VALUE).isValid())
        assertFalse(draft.copy(imageIds = listOf(91, 91)).isValid())
        assertFalse(draft.copy(account = draft.account.copy(number = "---")).isValid())
    }
}
