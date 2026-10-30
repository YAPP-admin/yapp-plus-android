package com.yapp.plus.data.notice

data class NoticeImageData(
    val imageKey: String,
    val filename: String,
    val readUrl: String,
    val readUrlExpiresAt: String,
    val displayOrder: Int,
)
