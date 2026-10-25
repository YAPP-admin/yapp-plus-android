package com.yapp.plus.feature.main.board.model

import com.yapp.plus.domain.notice.Notice
import com.yapp.plus.domain.notice.NoticeType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class BoardNoticeUiModelTest {
    @Test
    fun dateUsesKoreanDayAfterUtcMidnightBoundary() {
        val notice =
            Notice(
                id = 1,
                title = "세션 공지",
                content = "본문",
                type = NoticeType.SESSION,
                publishedAt = Instant.parse("2026-09-29T15:30:00Z"),
            )

        val result = notice.toUiModel()

        assertEquals("2026.09.30 (수)", result.date)
        assertEquals(BoardCategory.Session, result.category)
    }
}
