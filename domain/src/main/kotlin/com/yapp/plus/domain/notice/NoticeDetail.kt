package com.yapp.plus.domain.notice

import java.time.LocalDate

data class NoticeDetail(
    val id: Long,
    val title: String,
    val contentMarkdown: String,
    val type: NoticeType,
    val createdDate: LocalDate,
    val assignment: NoticeAssignment?,
    val images: List<NoticeImage>,
    val readCount: Long,
    val isRead: Boolean,
)
