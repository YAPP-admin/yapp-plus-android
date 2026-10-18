package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
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
            val viewModel = HomeViewModel(repository)

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
