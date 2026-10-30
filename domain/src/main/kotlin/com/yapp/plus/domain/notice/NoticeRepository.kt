package com.yapp.plus.domain.notice

interface NoticeRepository {
    suspend fun fetchNotices(
        type: NoticeType? = null,
        cursor: String? = null,
    ): NoticePage

    suspend fun fetchNotice(id: Long): NoticeDetail

    suspend fun markRead(id: Long)
}
