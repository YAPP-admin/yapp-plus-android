package com.yapp.plus.feature.main.schedule

import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun inactiveGenerationShowsNormalState() =
        runTest {
            val repository =
                FakeSessionScheduleRepository().apply {
                    generationFailure = ScheduleFailure.NoActiveGeneration
                }
            val viewModel = ScheduleViewModel(repository)
            advanceUntilIdle()
            assertEquals(ScheduleUiState.Inactive, viewModel.uiState.value)
        }

    @Test
    fun requestFailureShowsErrorAndRetryLoadsGenerationSchedule() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository =
                FakeSessionScheduleRepository().apply {
                    generationFailure = ScheduleFailure.Unavailable
                }
            val viewModel = ScheduleViewModel(repository)

            advanceUntilIdle()
            assertEquals(ScheduleUiState.Error, viewModel.uiState.value)

            repository.generationFailure = null
            viewModel.retry()
            advanceUntilIdle()

            assertEquals(2, repository.generationScheduleRequestCount)
            val content = viewModel.uiState.value as ScheduleUiState.Content
            assertEquals(1, content.value.size)
            assertEquals(ScheduleStatusUiModel.Upcoming, content.value.single().status)
        }

    @Test
    fun retriesDuringInitialLoadShareOneRequestAndCanReturnAnEmptySchedule() =
        runTest(mainDispatcherRule.dispatcher) {
            val gate = CompletableDeferred<Unit>()
            val repository =
                FakeSessionScheduleRepository().apply {
                    generationGate = gate
                    generationSchedule = generationSchedule.copy(sessions = emptyList())
                }
            val viewModel = ScheduleViewModel(repository)
            repeat(2) { viewModel.retry() }
            runCurrent()
            assertEquals(1, repository.generationScheduleRequestCount)
            assertEquals(ScheduleUiState.Loading, viewModel.uiState.value)

            gate.complete(Unit)
            advanceUntilIdle()
            assertEquals(ScheduleUiState.Empty, viewModel.uiState.value)
        }
}
