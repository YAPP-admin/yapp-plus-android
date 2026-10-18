package com.yapp.plus.feature.attendance

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.attendance.model.AttendanceScheduleStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceStatusUiModel
import com.yapp.plus.feature.attendance.model.AttendanceUiState
import com.yapp.plus.feature.attendance.model.CurrentWeekSessionUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceScheduleViewModelsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun homeRequestFailureShowsErrorAndRetryLoadsCurrentWeek() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository =
                FakeSessionScheduleRepository().apply {
                    currentWeekError = IllegalStateException("temporary failure")
                }
            val viewModel = AttendanceHomeViewModel(repository)

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
                    ),
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun scheduleRequestFailureShowsErrorAndRetryLoadsGenerationSchedule() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository =
                FakeSessionScheduleRepository().apply {
                    generationScheduleError = IllegalStateException("temporary failure")
                }
            val viewModel = AttendanceScheduleViewModel(repository)

            advanceUntilIdle()
            assertEquals(AttendanceUiState.Error, viewModel.uiState.value)

            repository.generationScheduleError = null
            viewModel.retry()
            advanceUntilIdle()

            assertEquals(2, repository.generationScheduleRequestCount)
            val content = viewModel.uiState.value as AttendanceUiState.Content
            assertEquals(1, content.value.size)
            assertEquals(AttendanceScheduleStatusUiModel.Upcoming, content.value.single().status)
        }
}

private class FakeSessionScheduleRepository : SessionScheduleRepository {
    var currentWeekError: Exception? = null
    var generationScheduleError: Exception? = null
    var currentWeekRequestCount = 0
    var generationScheduleRequestCount = 0

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

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationSchedule {
        generationScheduleRequestCount += 1
        generationScheduleError?.let { throw it }
        val date = LocalDate.now().plusDays(1)
        return CurrentGenerationSchedule(
            generationId = 19,
            generationNumber = 19,
            sessions =
                listOf(
                    ScheduledSession(
                        sessionId = 1,
                        name = "개발 세션",
                        date = date,
                        dayOfWeek = date.dayOfWeek,
                        location = null,
                        startTime = LocalTime.of(14, 0),
                        endTime = LocalTime.of(18, 0),
                        type = SessionType.REGULAR,
                    ),
                ),
        )
    }
}
