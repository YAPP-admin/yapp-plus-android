package com.yapp.plus.data.notice

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
)
