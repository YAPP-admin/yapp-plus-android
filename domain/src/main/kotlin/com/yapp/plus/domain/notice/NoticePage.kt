package com.yapp.plus.domain.notice

data class NoticePage(
    val totalCount: Long,
    val items: List<NoticeSummary>,
    val nextCursor: String?,
)
