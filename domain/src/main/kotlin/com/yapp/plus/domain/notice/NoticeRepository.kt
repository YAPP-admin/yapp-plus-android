package com.yapp.plus.domain.notice

interface NoticeRepository {
    /** 유형이 null이면 전체 공지를 게시 시각 내림차순으로 반환한다. */
    suspend fun fetchNotices(type: NoticeType? = null): List<Notice>

    /** 조회할 수 있는 공지가 없으면 null을 반환한다. */
    suspend fun fetchNotice(noticeId: Long): Notice?
}
