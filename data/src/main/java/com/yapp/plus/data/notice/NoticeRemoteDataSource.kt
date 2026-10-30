package com.yapp.plus.data.notice

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode

class NoticeRemoteDataSource(
    private val client: HttpClient,
) : NoticeDataSource {
    override suspend fun fetchNotices(
        type: String,
        cursor: String?,
    ): NoticePageData {
        val response =
            client
                .get("posts") {
                    parameter("type", type)
                    cursor?.let { parameter("cursor", it) }
                }.body<NoticePageResponse>()
        return NoticePageData(
            response.totalPostCount,
            response.items.map {
                NoticeSummaryData(
                    it.postId,
                    it.title,
                    it.type,
                    it.createdDate,
                    it.daysUntilDeadline,
                )
            },
            response.nextCursor,
        )
    }

    override suspend fun fetchNotice(id: Long): NoticeDetailData {
        val response = client.get("posts/$id").body<NoticeDetailResponse>()
        return response.toData()
    }

    override suspend fun markRead(id: Long) {
        check(client.put("posts/$id/read").status == HttpStatusCode.NoContent)
    }
}
