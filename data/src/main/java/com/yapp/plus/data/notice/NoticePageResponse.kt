package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticePageResponse(
    val totalPostCount: Long,
    val items: List<NoticeSummaryResponse>,
    val nextCursor: String?,
)
