package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType

class NoticeRepositoryImpl(
    private val source: NoticeDataSource,
) : NoticeRepository {
    override suspend fun fetchNotices(
        type: NoticeType?,
        cursor: String?,
    ): NoticePage = source.fetchNotices(type?.name ?: "ALL", cursor).toDomain()

    override suspend fun fetchNotice(id: Long): NoticeDetail {
        require(id > 0)
        val detail = source.fetchNotice(id)
        require(detail.postId == id)
        return detail.toDomain()
    }

    override suspend fun markRead(id: Long) {
        require(id > 0)
        source.markRead(id)
    }
}
