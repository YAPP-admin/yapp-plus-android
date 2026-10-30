package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticeImageResponse(
    val imageKey: String,
    val filename: String,
    val readUrl: String,
    val readUrlExpiresAt: String,
    val displayOrder: Int,
)
