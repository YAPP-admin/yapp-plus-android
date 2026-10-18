package com.yapp.plus.feature.main.schedule

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun requestFailureShowsErrorAndRetryLoadsGenerationSchedule() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository =
                FakeSessionScheduleRepository().apply {
                    generationScheduleError = IllegalStateException("temporary failure")
                }
            val viewModel = ScheduleViewModel(repository)

            advanceUntilIdle()
            assertEquals(ScheduleUiState.Error, viewModel.uiState.value)

            repository.generationScheduleError = null
            viewModel.retry()
            advanceUntilIdle()

            assertEquals(2, repository.generationScheduleRequestCount)
            val content = viewModel.uiState.value as ScheduleUiState.Content
            assertEquals(1, content.value.size)
            assertEquals(ScheduleStatusUiModel.Upcoming, content.value.single().status)
        }
}

private class FakeSessionScheduleRepository : SessionScheduleRepository {
    var generationScheduleError: Exception? = null
    var generationScheduleRequestCount = 0

    override suspend fun fetchCurrentWeekSchedule() =
        CurrentWeekSchedule(
            weekStartDate = LocalDate.of(2026, 10, 18),
            weekEndDate = LocalDate.of(2026, 10, 24),
            session = null,
        )

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
