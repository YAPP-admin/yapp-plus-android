package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyPage
import kotlinx.serialization.Serializable

@Serializable
internal data class CursorResponse<T>(
    val items: List<T>,
    val nextCursor: String?,
) {
    fun <R> toDomain(map: (T) -> R): AfterpartyPage<R> = AfterpartyPage(items.map(map), nextCursor)
}
