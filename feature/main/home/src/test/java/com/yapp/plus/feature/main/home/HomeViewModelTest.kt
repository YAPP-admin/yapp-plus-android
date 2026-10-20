package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.notice.Notice
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.toHomeNoticeState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun requestFailureShowsErrorAndRetryLoadsCurrentWeek() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository =
                FakeSessionScheduleRepository().apply {
                    currentWeekError = IllegalStateException("temporary failure")
                }
            val viewModel = HomeViewModel(repository, noticeRepository())

            advanceUntilIdle()
            assertEquals(AttendanceUiState.Error, viewModel.uiState.value)

            repository.currentWeekError = null
            viewModel.retry()
            advanceUntilIdle()

            assertEquals(2, repository.currentWeekRequestCount)
            assertEquals(
                AttendanceUiState.Content(
                    CurrentWeekSessionUiModel(
                        weekStartDateLabel = "10월 18일",
                        weekEndDateLabel = "10월 24일",
                        dateLabel = "10월 24일 (토)",
                        name = "개발 세션",
                        startTimeLabel = "오후 2시",
                        location = null,
                        attendanceStatus = AttendanceStatusUiModel.NotOpen,
                        sessionId = 1,
                        weekStartDate = LocalDate.of(2026, 10, 18),
                        sessionDate = LocalDate.of(2026, 10, 24),
                        startTime = LocalTime.of(14, 0),
                    ),
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun noticeFailureRetriesIndependentlyAndIgnoresDuplicateRequests() =
        runTest {
            val response = CompletableDeferred<List<Notice>>()
            var calls = 0
            val viewModel =
                HomeViewModel(
                    FakeSessionScheduleRepository(),
                    noticeRepository {
                        if (++calls == 1) error("sensitive provider detail")
                        response.await()
                    },
                )
            assertEquals(HomeNoticeUiState.Error, viewModel.notices.value)
            viewModel.retryNotices()
            viewModel.retryNotices()
            assertEquals(2, calls)
            assertEquals(HomeNoticeUiState.Loading, viewModel.notices.value)
            response.complete(listOf(notice(1, Instant.now())))
            advanceUntilIdle()
            assertEquals(
                HomeNoticeUiState.Content(listOf(HomeNoticeUiModel(1, "공지", false))),
                viewModel.notices.value,
            )
        }

    @Test
    fun homeIncludesOnlyTheKoreanSundayWeekAndPreservesPublicationOrder() {
        val start = Instant.parse("2026-10-17T15:00:00Z")
        val end = Instant.parse("2026-10-24T15:00:00Z")
        val notices =
            listOf(
                notice(4, end),
                notice(3, end.minusNanos(1), NoticeType.SESSION),
                notice(2, start),
                notice(1, start.minusNanos(1)),
            )
        val state =
            notices.toHomeNoticeState(LocalDate.of(2026, 10, 24)) as HomeNoticeUiState.Content
        assertEquals(listOf(3L, 2L), state.notices.map { it.id })
        assertEquals(listOf(true, false), state.notices.map { it.isSessionNotice })
        assertEquals(HomeNoticeUiState.Empty, notices.toHomeNoticeState(LocalDate.of(2026, 11, 1)))
    }

    private fun notice(
        id: Long,
        date: Instant,
        type: NoticeType = NoticeType.OPERATION,
    ) = Notice(id, "공지", "본문", type, date)

    private fun noticeRepository(fetch: suspend () -> List<Notice> = { emptyList() }) =
        object : NoticeRepository {
            override suspend fun fetchNotices(type: NoticeType?) = fetch()

            override suspend fun fetchNotice(noticeId: Long): Notice? = null
        }
}

private class FakeSessionScheduleRepository : SessionScheduleRepository {
    var currentWeekError: Exception? = null
    var currentWeekRequestCount = 0

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule {
        currentWeekRequestCount += 1
        currentWeekError?.let { throw it }
        return CurrentWeekSchedule(
            weekStartDate = LocalDate.of(2026, 10, 18),
            weekEndDate = LocalDate.of(2026, 10, 24),
            session =
                CurrentWeekSession(
                    sessionId = 1,
                    name = "개발 세션",
                    date = LocalDate.of(2026, 10, 24),
                    startTime = LocalTime.of(14, 0),
                    location = null,
                    attendanceStatus = AttendanceStatus.NOT_OPEN,
                ),
        )
    }

    override suspend fun fetchCurrentGenerationSchedule() =
        CurrentGenerationSchedule(
            generationId = 19,
            generationNumber = 19,
            sessions = emptyList(),
        )
}
