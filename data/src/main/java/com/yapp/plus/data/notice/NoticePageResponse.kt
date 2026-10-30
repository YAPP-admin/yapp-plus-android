package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticePageResponse(
    val totalPostCount: Long,
    val items: List<NoticeSummaryResponse>,
    val nextCursor: String?,
) {
    fun toData(): NoticePageData =
        NoticePageData(
            totalPostCount = totalPostCount,
            items = items.map { it.toData() },
            nextCursor = nextCursor,
        )
}
