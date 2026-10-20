package com.yapp.plus.data.notice

import com.yapp.plus.data.supabase.SupabaseRestClient
import com.yapp.plus.domain.notice.NoticeType
import kotlinx.serialization.json.Json

class SupabaseNoticeDataSource(
    private val client: SupabaseRestClient,
) : NoticeDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchNotices(type: NoticeType?): List<NoticeData> {
        val parameters = mutableListOf("order" to "published_at.desc,id.desc")
        if (type != null) parameters += "notice_type" to "eq.${type.name}"
        return client.fetch("mvp_notices", parameters) { body ->
            json.decodeFromString<List<SupabaseNoticeResponse>>(body).map { it.toData() }
        }
    }

    override suspend fun fetchNotice(noticeId: Long): NoticeData? =
        client.fetch("mvp_notices", listOf("id" to "eq.$noticeId", "limit" to "1")) { body ->
            val rows = json.decodeFromString<List<SupabaseNoticeResponse>>(body)
            require(rows.size <= 1)
            rows.firstOrNull()?.toData()?.also { require(it.id == noticeId) }
        }
}
