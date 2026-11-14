package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun requestFailureShowsErrorAndRetryLoadsCurrentWeek() =
        runTest(mainDispatcherRule.dispatcher) {
            val scheduleRepository =
                FakeSessionScheduleRepository().apply {
                    currentWeekError = IllegalStateException("temporary failure")
                }
            val noticeRepository = FakeHomeNoticeRepository()
            val viewModel = HomeViewModel(scheduleRepository, noticeRepository)

            viewModel.loadHomeData()
            advanceUntilIdle()
            assertEquals(AttendanceUiState.Error, viewModel.uiState.value.currentWeekSchedule)
            assertFalse(viewModel.uiState.value.isLoading)

            scheduleRepository.currentWeekError = null
            viewModel.retryCurrentWeekSchedule()
            advanceUntilIdle()

            assertEquals(2, scheduleRepository.currentWeekRequestCount)
            assertEquals(1, noticeRepository.requestCount)
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
                viewModel.uiState.value.currentWeekSchedule,
            )
        }

    @Test
    fun homeRequestsRunInParallelAndShareOneLoadingState() =
        runTest(mainDispatcherRule.dispatcher) {
            val scheduleGate = CompletableDeferred<Unit>()
            val noticeGate = CompletableDeferred<Unit>()
            val scheduleRepository = FakeSessionScheduleRepository().apply { gate = scheduleGate }
            val noticeRepository = FakeHomeNoticeRepository().apply { gate = noticeGate }
            val viewModel = HomeViewModel(scheduleRepository, noticeRepository)

            repeat(2) { viewModel.loadHomeData() }
            runCurrent()
            assertTrue(scheduleRepository.requestStarted.isCompleted)
            assertTrue(noticeRepository.requestStarted.isCompleted)
            assertEquals(1, scheduleRepository.currentWeekRequestCount)
            assertEquals(1, noticeRepository.requestCount)
            assertTrue(viewModel.uiState.value.isLoading)

            scheduleGate.complete(Unit)
            runCurrent()
            assertTrue(viewModel.uiState.value.isLoading)

            noticeGate.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun weeklyNoticesFilterToCurrentWeekAndRetryAfterFailure() =
        runTest(mainDispatcherRule.dispatcher) {
            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val noticeRepository =
                FakeHomeNoticeRepository().apply {
                    noticeError = IllegalStateException("temporary failure")
                }
            val viewModel = HomeViewModel(FakeSessionScheduleRepository(), noticeRepository)

            viewModel.loadHomeData()
            advanceUntilIdle()
            assertEquals(HomeNoticeUiState.Error, viewModel.uiState.value.weeklyNotices)

            noticeRepository.noticeError = null
            noticeRepository.page =
                NoticePage(
                    totalCount = 6,
                    items =
                        (1L..5L).map {
                            NoticeSummary(it, "공지", NoticeType.ASSIGNMENT, today, 2)
                        } +
                            NoticeSummary(
                                6,
                                "지난 주",
                                NoticeType.SESSION,
                                today.minusWeeks(1),
                                null,
                            ),
                    nextCursor = null,
                )
            viewModel.retryWeeklyNotices()
            advanceUntilIdle()

            val notices = viewModel.uiState.value.weeklyNotices as HomeNoticeUiState.Content
            assertEquals(listOf(1L, 2L, 3L), notices.notices.map { it.id })
            assertTrue(notices.notices.all { it.isAssignmentNotice })
            assertEquals(2, noticeRepository.requestCount)
        }
}

private class FakeSessionScheduleRepository : SessionScheduleRepository {
    var currentWeekError: Exception? = null
    var currentWeekRequestCount = 0
    var gate: CompletableDeferred<Unit>? = null
    val requestStarted = CompletableDeferred<Unit>()

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule {
        currentWeekRequestCount += 1
        requestStarted.complete(Unit)
        gate?.await()
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

private class FakeHomeNoticeRepository : NoticeRepository {
    var noticeError: Exception? = null
    var requestCount = 0
    var gate: CompletableDeferred<Unit>? = null
    var page = NoticePage(0, emptyList(), null)
    val requestStarted = CompletableDeferred<Unit>()

    override suspend fun fetchNotices(
        type: NoticeType?,
        cursor: String?,
    ): NoticePage {
        requestCount += 1
        requestStarted.complete(Unit)
        gate?.await()
        noticeError?.let { throw it }
        return page
    }

    override suspend fun fetchNotice(id: Long): NoticeDetail = error("unused")

    override suspend fun markRead(id: Long) = error("unused")
}
