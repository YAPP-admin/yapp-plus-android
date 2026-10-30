package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class HomeNoticeViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun weeklyNoticesUseServerDatesAndPreserveAssignmentTypeAfterRetry() =
        runTest {
            var shouldFail = true
            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val repository =
                object : NoticeRepository {
                    override suspend fun fetchNotices(
                        type: NoticeType?,
                        cursor: String?,
                    ): NoticePage {
                        if (shouldFail) error("offline")
                        val items =
                            (1L..5L).map {
                                NoticeSummary(it, "공지", NoticeType.ASSIGNMENT, today, 2)
                            } +
                                NoticeSummary(
                                    6,
                                    "지난 주",
                                    NoticeType.SESSION,
                                    today.minusWeeks(1),
                                    null,
                                )
                        return NoticePage(6, items, null)
                    }

                    override suspend fun fetchNotice(id: Long): NoticeDetail = error("unused")

                    override suspend fun markRead(id: Long) = error("unused")
                }
            val model = HomeNoticeViewModel(repository)
            model.fetchNotices()
            assertEquals(HomeNoticeUiState.Error, model.uiState.value)
            shouldFail = false
            model.fetchNotices()
            val notices = (model.uiState.value as HomeNoticeUiState.Content).notices
            assertEquals(listOf(1L, 2L, 3L), notices.map { it.id })
            assertTrue(notices.all { it.isAssignmentNotice })
        }
}
