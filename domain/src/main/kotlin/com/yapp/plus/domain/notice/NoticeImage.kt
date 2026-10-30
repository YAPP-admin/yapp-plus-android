package com.yapp.plus.domain.notice

import java.time.Instant

data class NoticeImage(
    val key: String,
    val filename: String,
    val url: String,
    val expiresAt: Instant,
)
