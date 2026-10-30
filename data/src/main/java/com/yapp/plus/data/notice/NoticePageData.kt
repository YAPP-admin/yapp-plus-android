package com.yapp.plus.data.notice

data class NoticePageData(
    val totalPostCount: Long,
    val items: List<NoticeSummaryData>,
    val nextCursor: String?,
)
