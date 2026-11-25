package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementAccount
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class SettlementDetailResponse(
    val settlementId: Long,
    val afterpartyId: Long,
    val creatorName: String,
    val isMe: Boolean,
    val totalAmount: Long,
    val perPersonAmount: Long,
    val bank: SettlementBank,
    val accountNumber: String,
    val accountHolder: String,
    val participantCount: Int,
    val debtorCount: Int,
    val paidCount: Int,
    val myAmount: Long,
    val paymentStatus: SettlementPaymentStatus?,
    val recipientIds: Set<Long>,
    val photos: List<SettlementPhotoResponse>,
    val revision: Long,
) {
    fun toDomain(): SettlementDetail =
        SettlementDetail(
            item =
                SettlementItem(
                    settlementId,
                    afterpartyId,
                    creatorName,
                    isMe,
                    participantCount,
                    myAmount,
                    paymentStatus,
                ),
            account = SettlementAccount(bank, accountNumber, accountHolder),
            totalAmount = totalAmount,
            perPersonAmount = perPersonAmount,
            debtorCount = debtorCount,
            paidCount = paidCount,
            recipientIds = recipientIds,
            photos = photos.map(SettlementPhotoResponse::toDomain),
            revision = revision,
        )
}
