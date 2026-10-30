package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticeType
import java.time.LocalDate

data class NoticeDetailData(
    val postId: Long,
    val title: String,
    val contentMarkdown: String,
    val type: String,
    val createdDate: String,
    val assignment: NoticeAssignmentData?,
    val images: List<NoticeImageData>,
    val readCount: Long,
    val read: Boolean,
) {
    internal fun toDomain(): NoticeDetail {
        require(title.isNotBlank() && readCount >= 0)
        return NoticeDetail(
            postId,
            title,
            contentMarkdown,
            NoticeType.valueOf(type),
            LocalDate.parse(createdDate),
            assignment?.toDomain(),
            images.sortedBy { it.displayOrder }.map { it.toDomain() },
            readCount,
            read,
        )
    }
}
