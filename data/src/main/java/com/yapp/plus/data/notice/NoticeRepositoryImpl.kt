package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.Notice
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType

class NoticeRepositoryImpl(
    private val dataSource: NoticeDataSource,
) : NoticeRepository {
    override suspend fun fetchNotices(type: NoticeType?): List<Notice> =
        dataSource
            .fetchNotices(type)
            .map(::toDomain)

    override suspend fun fetchNotice(noticeId: Long): Notice? =
        dataSource
            .fetchNotice(noticeId)
            ?.let(::toDomain)

    private fun toDomain(data: NoticeData): Notice =
        Notice(
            id = data.id,
            title = data.title,
            content = data.content,
            type = data.type,
            publishedAt = data.publishedAt,
        )
}
