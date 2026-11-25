package com.yapp.plus.domain.afterparty

import java.time.Instant

data class SettlementPhoto(
    val id: Long,
    val url: String?,
    val expiresAt: Instant?,
) {
    fun activeUrl(now: Instant): String? = url?.takeIf { expiresAt?.isAfter(now) == true }

    override fun toString(): String = "SettlementPhoto(id=$id, expiresAt=$expiresAt)"
}
