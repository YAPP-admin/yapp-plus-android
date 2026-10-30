package com.yapp.plus.data.notice

interface NoticeDataSource {
    suspend fun fetchNotices(
        type: String,
        cursor: String?,
    ): NoticePageData

    suspend fun fetchNotice(id: Long): NoticeDetailData

    suspend fun markRead(id: Long)
}
