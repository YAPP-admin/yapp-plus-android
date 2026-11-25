package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus

internal interface SettlementDataSource {
    suspend fun fetchSettlements(
        afterpartyId: Long,
        cursor: String?,
    ): CursorResponse<SettlementItemResponse>

    suspend fun fetchSettlement(
        afterpartyId: Long,
        settlementId: Long,
        edit: Boolean,
    ): SettlementDetailResponse

    suspend fun fetchCandidates(
        afterpartyId: Long,
        settlementId: Long?,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse>

    suspend fun uploadPhoto(
        afterpartyId: Long,
        settlementId: Long?,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): Long

    suspend fun saveSettlement(
        afterpartyId: Long,
        settlementId: Long?,
        expectedRevision: Long?,
        draft: SettlementDraft,
    ): SettlementDetailResponse

    suspend fun updatePayment(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
    ): SettlementPaymentResponse

    suspend fun fetchSummary(
        afterpartyId: Long,
        settlementId: Long,
    ): SettlementSummaryResponse

    suspend fun fetchRecipients(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse>
}
