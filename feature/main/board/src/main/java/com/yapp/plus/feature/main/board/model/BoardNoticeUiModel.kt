package com.yapp.plus.feature.main.board.model

import com.yapp.plus.domain.notice.NoticeAssignment
import com.yapp.plus.domain.notice.NoticeImage

data class BoardNoticeUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val category: BoardCategory,
    val date: String,
    val daysUntilDeadline: Long? = null,
    val assignment: NoticeAssignment? = null,
    val images: List<NoticeImage> = emptyList(),
    val readCount: Long = 0,
    val isRead: Boolean = false,
)
