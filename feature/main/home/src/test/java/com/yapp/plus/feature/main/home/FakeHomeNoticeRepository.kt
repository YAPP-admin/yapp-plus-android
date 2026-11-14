package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import kotlinx.coroutines.CompletableDeferred

internal class FakeHomeNoticeRepository : NoticeRepository {
    var noticeError: Exception? = null
    var requestCount = 0
    var gate: CompletableDeferred<Unit>? = null
    var page = NoticePage(0, emptyList(), null)

    override suspend fun fetchNotices(
        type: NoticeType?,
        cursor: String?,
    ): NoticePage {
        requestCount += 1
        gate?.await()
        noticeError?.let { throw it }
        return page
    }

    override suspend fun fetchNotice(id: Long): NoticeDetail = error("unused")

    override suspend fun markRead(id: Long) = error("unused")
}
