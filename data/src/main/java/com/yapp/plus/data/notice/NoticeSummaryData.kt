package com.yapp.plus.data.notice

data class NoticeSummaryData(
    val postId: Long,
    val title: String,
    val type: String,
    val createdDate: String,
    val daysUntilDeadline: Long?,
)
