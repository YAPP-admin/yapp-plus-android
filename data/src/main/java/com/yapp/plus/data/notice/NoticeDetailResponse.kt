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
    fun toData() =
        NoticeDetailData(
            postId,
            title,
            contentMarkdown,
            type,
            createdDate,
            assignment?.let {
                NoticeAssignmentData(
                    it.deadlineDate,
                    it.positions,
                    it.submissionUrl,
                    it.submissionLinkText,
                )
            },
            images.map {
                NoticeImageData(
                    it.imageKey,
                    it.filename,
                    it.readUrl,
                    it.readUrlExpiresAt,
                    it.displayOrder,
                )
            },
            readCount,
            read,
        )
}
