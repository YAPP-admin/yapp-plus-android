package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeImage
import java.time.Instant

data class NoticeImageData(
    val imageKey: String,
    val filename: String,
    val readUrl: String,
    val readUrlExpiresAt: String,
    val displayOrder: Int,
) {
    internal fun toDomain(): NoticeImage =
        NoticeImage(
            imageKey,
            filename,
            validatedNoticeWebUrl(readUrl),
            Instant.parse(readUrlExpiresAt),
        )
}
