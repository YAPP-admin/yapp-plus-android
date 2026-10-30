package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticeSummaryResponse(
    val postId: Long,
    val title: String,
    val type: String,
    val createdDate: String,
    val daysUntilDeadline: Long?,
) {
    fun toData(): NoticeSummaryData =
        NoticeSummaryData(
            postId = postId,
            title = title,
            type = type,
            createdDate = createdDate,
            daysUntilDeadline = daysUntilDeadline,
        )
}
