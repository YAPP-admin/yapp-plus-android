package com.yapp.plus.feature.main.board

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import java.time.LocalDate

internal class FakeNoticeRepository : NoticeRepository {
    var fetchPage: suspend (NoticeType?, String?) -> NoticePage = { _, _ -> page(1) }
    var fetchDetail: suspend (Long) -> NoticeDetail = { detail(it) }
    var read: suspend (Long) -> Unit = {}
    var readRequests = 0

    override suspend fun fetchNotices(
        type: NoticeType?,
        cursor: String?,
    ) = fetchPage(type, cursor)

    override suspend fun fetchNotice(id: Long) = fetchDetail(id)

    override suspend fun markRead(id: Long) {
        readRequests++
        read(id)
    }

    fun page(
        vararg ids: Long,
        cursor: String? = null,
    ) = NoticePage(
        ids.size.toLong(),
        ids.map {
            NoticeSummary(it, "공지 $it", NoticeType.SESSION, LocalDate.of(2026, 10, 30), null)
        },
        cursor,
    )

    fun detail(
        id: Long,
        isRead: Boolean = false,
        count: Long = 0,
    ) = NoticeDetail(
        id,
        "공지 $id",
        "본문",
        NoticeType.SESSION,
        LocalDate.of(2026, 10, 30),
        null,
        emptyList(),
        count,
        isRead,
    )
}
