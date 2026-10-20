package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeType
import java.time.Instant

data class NoticeData(
    val id: Long,
    val title: String,
    val content: String,
    val type: NoticeType,
    val publishedAt: Instant,
)
