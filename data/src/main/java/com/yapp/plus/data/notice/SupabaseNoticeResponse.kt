package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
internal data class SupabaseNoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    @SerialName("notice_type") val noticeType: String,
    @SerialName("published_at") val publishedAt: String,
) {
    fun toData(): NoticeData {
        require(id > 0 && title.isNotBlank())
        return NoticeData(
            id = id,
            title = title,
            content = content,
            type = NoticeType.valueOf(noticeType),
            publishedAt = Instant.parse(publishedAt),
        )
    }
}
