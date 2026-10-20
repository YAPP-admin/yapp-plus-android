package com.yapp.plus.domain.notice

import java.time.Instant

data class Notice(
    val id: Long,
    val title: String,
    val content: String,
    val type: NoticeType,
    val publishedAt: Instant,
)
