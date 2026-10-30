package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticePage

data class NoticePageData(
    val totalPostCount: Long,
    val items: List<NoticeSummaryData>,
    val nextCursor: String?,
) {
    internal fun toDomain(): NoticePage {
        require(totalPostCount >= 0 && (nextCursor == null || nextCursor.isNotBlank()))
        return NoticePage(totalPostCount, items.map { it.toDomain() }, nextCursor)
    }
}
