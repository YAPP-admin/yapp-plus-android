package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.feature.main.home.model.SessionDistanceUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeDistanceViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val target = SessionCoordinates(latitude = 37.58, longitude = 126.88)

    @Test
    fun repeatedTapRequestsOneDistanceAndKeepsApproximateStatus() =
        runTest(mainDispatcherRule.dispatcher) {
            var requests = 0
            val measurement = CompletableDeferred<SessionDistance>()
            val viewModel =
                HomeDistanceViewModel {
                    assertEquals(target, it)
                    requests++
                    measurement.await()
                }

            repeat(2) { viewModel.measure(target) }
            runCurrent()
            assertEquals(1, requests)
            assertEquals(SessionDistanceUiState.Loading, viewModel.uiState.value)

            val distance = SessionDistance.Measured(meters = 310, isApproximate = true)
            measurement.complete(distance)
            advanceUntilIdle()
            assertEquals(SessionDistanceUiState.Result(distance), viewModel.uiState.value)
        }

    @Test
    fun cancellationDiscardsTheOldMeasurementAndAllowsAnotherAttempt() =
        runTest(mainDispatcherRule.dispatcher) {
            val previousMeasurement = CompletableDeferred<SessionDistance>()
            val nextTarget = target.copy(latitude = 37.59)
            val nextDistance = SessionDistance.Measured(meters = 24, isApproximate = false)
            val viewModel =
                HomeDistanceViewModel {
                    if (it == target) previousMeasurement.await() else nextDistance
                }

            viewModel.measure(target)
            runCurrent()
            viewModel.clear()
            assertEquals(SessionDistanceUiState.Idle, viewModel.uiState.value)

            viewModel.measure(nextTarget)
            previousMeasurement.complete(SessionDistance.LocationDisabled)
            advanceUntilIdle()
            assertEquals(SessionDistanceUiState.Result(nextDistance), viewModel.uiState.value)
        }

    @Test
    fun deniedPermissionAndProviderFailureLeaveRetryAvailable() =
        runTest(mainDispatcherRule.dispatcher) {
            var isAvailable = false
            val result = SessionDistance.Measured(meters = 14, isApproximate = false)
            val viewModel =
                HomeDistanceViewModel {
                    if (isAvailable) result else error("provider failed")
                }

            viewModel.onPermissionDenied()
            assertEquals(
                SessionDistanceUiState.Result(SessionDistance.PermissionRequired),
                viewModel.uiState.value,
            )
            viewModel.measure(target)
            advanceUntilIdle()
            assertEquals(
                SessionDistanceUiState.Result(SessionDistance.Unavailable),
                viewModel.uiState.value,
            )

            isAvailable = true
            viewModel.measure(target)
            advanceUntilIdle()
            assertEquals(SessionDistanceUiState.Result(result), viewModel.uiState.value)
        }
}
