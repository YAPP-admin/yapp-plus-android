package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import java.security.MessageDigest
import java.time.Instant

internal class SettlementRemoteDataSource(
    private val client: HttpClient,
    private val uploadClient: HttpClient,
) : SettlementDataSource {
    override suspend fun fetchSettlements(
        afterpartyId: Long,
        cursor: String?,
    ): CursorResponse<SettlementItemResponse> =
        client.afterpartyGet(listPath(afterpartyId)) { cursor?.let { append("cursor", it) } }

    override suspend fun fetchSettlement(
        afterpartyId: Long,
        settlementId: Long,
        edit: Boolean,
    ): SettlementDetailResponse {
        val path = detailPath(afterpartyId, settlementId)
        return client.afterpartyGet(if (edit) "$path/edit" else path)
    }

    override suspend fun fetchCandidates(
        afterpartyId: Long,
        settlementId: Long?,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse> =
        client.afterpartyGet("afterparties/$afterpartyId/settlement-participants") {
            settlementId?.let { append("settlementId", it.toString()) }
            cursor?.let { append("cursor", it) }
        }

    override suspend fun uploadPhoto(
        afterpartyId: Long,
        settlementId: Long?,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): Long {
        val request =
            buildJsonObject {
                put("filename", filename)
                put("contentType", contentType)
                put("sizeBytes", bytes.size.toLong())
                put("checksumSha256", MessageDigest.getInstance("SHA-256").digest(bytes).encodeBase64())
                settlementId?.let { put("settlementId", it) }
            }
        val reservation =
            client.afterpartyPost<SettlementPhotoReservation>(
                "afterparties/$afterpartyId/settlement-images/presign",
                request,
            )
        require(reservation.imageId > 0 && isSecurePhotoUrl(reservation.uploadUrl))
        check(Instant.parse(reservation.expiresAt).isAfter(Instant.now()))
        val response =
            uploadClient.put(reservation.uploadUrl) {
                reservation.signedHeaders.forEach { (name, value) -> headers.append(name, value) }
                setBody(bytes)
            }
        check(response.status.value in 200..299)
        return reservation.imageId
    }

    override suspend fun saveSettlement(
        afterpartyId: Long,
        settlementId: Long?,
        expectedRevision: Long?,
        draft: SettlementDraft,
    ): SettlementDetailResponse {
        val request =
            buildJsonObject {
                put("bank", draft.account.bank.name)
                put("accountNumber", draft.account.number)
                put("accountHolder", draft.account.holder)
                put("totalAmount", draft.totalAmount)
                put("perPersonAmount", draft.perPersonAmount)
                putJsonArray("recipientIds") { draft.recipientIds.forEach { add(JsonPrimitive(it)) } }
                putJsonArray("imageIds") { draft.imageIds.forEach { add(JsonPrimitive(it)) } }
                expectedRevision?.let { put("expectedRevision", it) }
            }
        return if (settlementId == null) {
            client.afterpartyPost(listPath(afterpartyId), request)
        } else {
            client.afterpartyPut(detailPath(afterpartyId, settlementId), request)
        }
    }

    override suspend fun updatePayment(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
    ): SettlementPaymentResponse =
        client.afterpartyPut(
            "${detailPath(afterpartyId, settlementId)}/payment",
            buildJsonObject { put("paymentStatus", status.name) },
        )

    override suspend fun fetchSummary(
        afterpartyId: Long,
        settlementId: Long,
    ): SettlementSummaryResponse = client.afterpartyGet("${detailPath(afterpartyId, settlementId)}/summary")

    override suspend fun fetchRecipients(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse> =
        client.afterpartyGet("${detailPath(afterpartyId, settlementId)}/recipients") {
            append("paymentStatus", status.name)
            cursor?.let { append("cursor", it) }
        }

    private fun listPath(afterpartyId: Long): String = "afterparties/$afterpartyId/settlements"

    private fun detailPath(
        afterpartyId: Long,
        settlementId: Long,
    ): String = "${listPath(afterpartyId)}/$settlementId"
}
