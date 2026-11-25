package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementPhoto
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
internal data class SettlementPhotoResponse(
    val imageId: Long,
    val readUrl: String?,
    val expiresAt: String?,
) {
    fun toDomain(): SettlementPhoto =
        SettlementPhoto(imageId, readUrl?.takeIf(::isSecurePhotoUrl), expiresAt?.let(Instant::parse))
}
