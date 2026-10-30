package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticeDetailResponse(
    val postId: Long,
    val title: String,
    val contentMarkdown: String,
    val type: String,
    val createdDate: String,
    val assignment: NoticeAssignmentResponse?,
    val images: List<NoticeImageResponse>,
    val readCount: Long,
    val read: Boolean,
) {
    fun toData(): NoticeDetailData =
        NoticeDetailData(
            postId = postId,
            title = title,
            contentMarkdown = contentMarkdown,
            type = type,
            createdDate = createdDate,
            assignment = assignment?.toData(),
            images = images.map { it.toData() },
            readCount = readCount,
            read = read,
        )
}
