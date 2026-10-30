package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import java.time.LocalDate

data class NoticeSummaryData(
    val postId: Long,
    val title: String,
    val type: String,
    val createdDate: String,
    val daysUntilDeadline: Long?,
) {
    internal fun toDomain(): NoticeSummary {
        require(postId > 0 && title.isNotBlank())
        return NoticeSummary(
            postId,
            title,
            NoticeType.valueOf(type),
            LocalDate.parse(createdDate),
            daysUntilDeadline,
        )
    }
}
