package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeType

interface NoticeDataSource {
    suspend fun fetchNotices(type: NoticeType?): List<NoticeData>

    suspend fun fetchNotice(noticeId: Long): NoticeData?
}
