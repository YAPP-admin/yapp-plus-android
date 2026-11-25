package com.yapp.plus.domain.afterparty

interface SettlementRepository {
    suspend fun fetchSettlements(
        afterpartyId: Long,
        cursor: String? = null,
    ): AfterpartyResult<AfterpartyPage<SettlementItem>>

    suspend fun fetchSettlement(
        afterpartyId: Long,
        settlementId: Long,
    ): AfterpartyResult<SettlementDetail>

    suspend fun fetchEdit(
        afterpartyId: Long,
        settlementId: Long,
    ): AfterpartyResult<SettlementDetail>

    suspend fun fetchCandidates(
        afterpartyId: Long,
        settlementId: Long?,
        cursor: String? = null,
    ): AfterpartyResult<AfterpartyPage<AfterpartyPerson>>

    suspend fun uploadPhoto(
        afterpartyId: Long,
        settlementId: Long?,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): AfterpartyResult<Long>

    suspend fun createSettlement(
        afterpartyId: Long,
        draft: SettlementDraft,
    ): AfterpartyResult<SettlementDetail>

    suspend fun updateSettlement(
        afterpartyId: Long,
        settlementId: Long,
        expectedRevision: Long,
        draft: SettlementDraft,
    ): AfterpartyResult<SettlementDetail>

    suspend fun updatePayment(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
    ): AfterpartyResult<SettlementPaymentStatus>

    suspend fun fetchSummary(
        afterpartyId: Long,
        settlementId: Long,
    ): AfterpartyResult<SettlementSummary>

    suspend fun fetchRecipients(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
        cursor: String? = null,
    ): AfterpartyResult<AfterpartyPage<AfterpartyPerson>>
}
