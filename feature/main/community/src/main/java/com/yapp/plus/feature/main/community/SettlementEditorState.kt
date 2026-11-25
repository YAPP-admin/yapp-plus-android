package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.SettlementAccount
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementPhoto

internal data class SettlementEditorState(
    val candidates: List<AfterpartyPerson> = emptyList(),
    val selected: Set<Long> = emptySet(),
    val bank: SettlementBank? = null,
    val number: String = "",
    val holder: String = "",
    val totalText: String = "",
    val amountOverride: String? = null,
    val photos: List<SettlementPhoto> = emptyList(),
    val revision: Long? = null,
    val financialLocked: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val failure: AfterpartyFailure? = null,
    val blocked: Boolean = false,
    val savedId: Long? = null,
    val savedRecipientCount: Int? = null,
) {
    val peopleCount: Int get() = selected.size + 1

    val amountText: String
        get() = amountOverride ?: ((totalText.toLongOrNull() ?: 0) / peopleCount).toString()

    fun draft(imageIds: List<Long> = photos.map { it.id }): SettlementDraft? {
        val selectedBank = bank ?: return null
        return SettlementDraft(
            account = SettlementAccount(selectedBank, number.trim(), holder.trim()),
            totalAmount = totalText.toLongOrNull() ?: return null,
            perPersonAmount = amountText.toLongOrNull() ?: return null,
            recipientIds = selected,
            imageIds = imageIds,
        ).takeIf(SettlementDraft::isValid)
    }
}
