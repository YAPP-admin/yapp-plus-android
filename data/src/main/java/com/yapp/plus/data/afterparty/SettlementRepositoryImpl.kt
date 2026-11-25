package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import io.ktor.client.HttpClient

class SettlementRepositoryImpl internal constructor(
    private val source: SettlementDataSource,
) : SettlementRepository {
    override suspend fun fetchSettlements(
        afterpartyId: Long,
        cursor: String?,
    ) = afterpartyRequest { source.fetchSettlements(afterpartyId, cursor).toDomain(SettlementItemResponse::toDomain) }

    override suspend fun fetchSettlement(
        afterpartyId: Long,
        settlementId: Long,
    ) = afterpartyRequest { source.fetchSettlement(afterpartyId, settlementId, edit = false).toDomain() }

    override suspend fun fetchEdit(
        afterpartyId: Long,
        settlementId: Long,
    ) = afterpartyRequest { source.fetchSettlement(afterpartyId, settlementId, edit = true).toDomain() }

    override suspend fun fetchCandidates(
        afterpartyId: Long,
        settlementId: Long?,
        cursor: String?,
    ) = afterpartyRequest {
        source.fetchCandidates(afterpartyId, settlementId, cursor).toDomain(AfterpartyPersonResponse::toDomain)
    }

    override suspend fun uploadPhoto(
        afterpartyId: Long,
        settlementId: Long?,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ) = if (bytes.size in 1..SettlementDraft.MAX_PHOTO_BYTES && contentType in PHOTO_TYPES) {
        afterpartyRequest { source.uploadPhoto(afterpartyId, settlementId, filename, contentType, bytes) }
    } else {
        YappResult.Failure(AfterpartyFailure.InvalidRequest)
    }

    override suspend fun createSettlement(
        afterpartyId: Long,
        draft: SettlementDraft,
    ) = if (draft.isValid()) {
        afterpartyRequest { source.saveSettlement(afterpartyId, null, null, draft).toDomain() }
    } else {
        YappResult.Failure(AfterpartyFailure.InvalidRequest)
    }

    override suspend fun updateSettlement(
        afterpartyId: Long,
        settlementId: Long,
        expectedRevision: Long,
        draft: SettlementDraft,
    ) = if (expectedRevision >= 0 && draft.isValid()) {
        afterpartyRequest { source.saveSettlement(afterpartyId, settlementId, expectedRevision, draft).toDomain() }
    } else {
        YappResult.Failure(AfterpartyFailure.InvalidRequest)
    }

    override suspend fun updatePayment(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
    ) = afterpartyRequest { source.updatePayment(afterpartyId, settlementId, status).paymentStatus }

    override suspend fun fetchSummary(
        afterpartyId: Long,
        settlementId: Long,
    ) = afterpartyRequest { source.fetchSummary(afterpartyId, settlementId).toDomain() }

    override suspend fun fetchRecipients(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
        cursor: String?,
    ) = afterpartyRequest {
        source.fetchRecipients(afterpartyId, settlementId, status, cursor).toDomain(AfterpartyPersonResponse::toDomain)
    }

    companion object {
        private val PHOTO_TYPES = setOf("image/jpeg", "image/png")

        fun create(
            client: HttpClient,
            uploadClient: HttpClient,
        ): SettlementRepository = SettlementRepositoryImpl(SettlementRemoteDataSource(client, uploadClient))
    }
}
