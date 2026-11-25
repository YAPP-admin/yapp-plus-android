package com.yapp.plus.feature.main.home

import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceLocationReading
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.domain.session.SessionDistanceRepository
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.HomeSubmissionStage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeInteractionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session =
        CurrentWeekSessionUiModel(
            weekStartDateLabel = "",
            weekEndDateLabel = "",
            dateLabel = "",
            name = "정규 세션",
            startTimeLabel = "",
            location = "DMC 첨단산업센터",
            attendanceStatus = AttendanceStatusUiModel.Available,
            sessionId = 42L,
            coordinates = SessionCoordinates(latitude = 37.58, longitude = 126.88),
        )
    private val allowedDistance =
        AttendanceLocationReading.Measured(meters = 100f, accuracyMeters = 50f)

    @Test
    fun attendanceUsesVerificationOnlyAndBlocksChangesUntilServerCompletion() =
        runTest {
            val response = CompletableDeferred<Unit>()
            val requests = mutableListOf<Pair<Long, Int>>()
            val viewModel =
                viewModel { sessionId, code ->
                    requests += sessionId to code
                    response.await()
                }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("0a1２239")
            viewModel.confirmAttendance(session, hasPrecisePermission = true)

            assertTrue(viewModel.uiState.value.isSubmitting)
            assertNull(viewModel.uiState.value.verifiedSessionId)
            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            viewModel.dismissSheet()
            viewModel.updateCode("9999")
            viewModel.openAttendanceCode(session.sessionId)
            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertEquals("0123", viewModel.uiState.value.code)

            response.complete(Unit)
            advanceUntilIdle()

            assertEquals(listOf(42L to 123), requests)
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            assertEquals(42L, viewModel.uiState.value.verifiedSessionId)
            assertFalse(viewModel.uiState.value.isSubmitting)
            viewModel.acknowledgeAttendanceComplete()
            assertNull(viewModel.uiState.value.verifiedSessionId)
        }

    @Test
    fun invalidSessionAndIncompleteCodeDoNotSubmitAndReopeningClearsInput() =
        runTest {
            var requests = 0
            val viewModel = viewModel { _, _ -> requests += 1 }
            viewModel.openAttendanceCode(0)
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("12")

            assertFalse(viewModel.confirmAttendance(session, hasPrecisePermission = false))
            assertEquals(R.string.home_code_required, viewModel.uiState.value.errorResource)
            assertTrue(viewModel.uiState.value.isInvalidCode)
            assertEquals("12", viewModel.uiState.value.code)
            viewModel.dismissSheet()
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            viewModel.openAttendanceCode(session.sessionId)

            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertEquals("", viewModel.uiState.value.code)
            assertNull(viewModel.uiState.value.errorResource)
            assertFalse(viewModel.uiState.value.isInvalidCode)
            assertEquals(0, requests)
        }

    @Test
    fun codeAndRequestErrorsPreserveInputAndCanRetryVerification() =
        runTest {
            var requests = 0
            val viewModel =
                viewModel { _, _ ->
                    when (++requests) {
                        1 -> throw AttendanceException(isInvalidCode = true)
                        2 -> error("provider failure")
                    }
                }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("1234")
            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            advanceUntilIdle()

            assertEquals(R.string.home_code_invalid, viewModel.uiState.value.errorResource)
            assertTrue(viewModel.uiState.value.isInvalidCode)
            assertEquals("1234", viewModel.uiState.value.code)
            assertNull(viewModel.uiState.value.verifiedSessionId)
            viewModel.updateCode("2345")
            assertNull(viewModel.uiState.value.errorResource)
            assertFalse(viewModel.uiState.value.isInvalidCode)
            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            advanceUntilIdle()

            assertEquals(R.string.home_submit_error, viewModel.uiState.value.errorResource)
            assertFalse(viewModel.uiState.value.isInvalidCode)
            assertEquals("2345", viewModel.uiState.value.code)
            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertNull(viewModel.uiState.value.verifiedSessionId)
            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            advanceUntilIdle()

            assertEquals(3, requests)
            assertEquals(42L, viewModel.uiState.value.verifiedSessionId)
            assertNull(viewModel.uiState.value.errorResource)
        }

    @Test
    fun missingOrChangedSessionDoesNotRequestPermissionOrSubmit() =
        runTest {
            val distanceRepository = preciseRepository { _ -> error("unexpected location") }
            val viewModel = viewModel(distanceRepository) { _, _ -> error("unexpected submission") }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("1234")
            for (unavailable in listOf(session.copy(coordinates = null), session.copy(sessionId = 43))) {
                assertFalse(viewModel.confirmAttendance(unavailable, hasPrecisePermission = false))
                assertEquals(HomeAttendanceLocationIssue.SessionUnavailable, viewModel.uiState.value.locationIssue)
                assertEquals("1234", viewModel.uiState.value.code)
            }
        }

    @Test
    fun permissionResultRequiresPrecisePermissionAndSameSession() =
        runTest {
            var measurements = 0
            var submissions = 0
            val distanceRepository =
                preciseRepository { _ -> allowedDistance.also { measurements++ } }
            val viewModel = viewModel(distanceRepository) { _, _ -> submissions++ }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("1234")
            assertTrue(viewModel.confirmAttendance(session, hasPrecisePermission = false))
            viewModel.onAttendancePermissionResult(
                session,
                hasPrecisePermission = false,
                hasApproximatePermission = false,
            )
            assertEquals(HomeAttendanceLocationIssue.PermissionDenied, viewModel.uiState.value.locationIssue)

            assertTrue(viewModel.confirmAttendance(session, hasPrecisePermission = false))
            viewModel.onAttendancePermissionResult(
                session,
                hasPrecisePermission = false,
                hasApproximatePermission = true,
            )
            assertEquals(
                HomeAttendanceLocationIssue.PrecisePermissionRequired,
                viewModel.uiState.value.locationIssue,
            )

            assertTrue(viewModel.confirmAttendance(session, hasPrecisePermission = false))
            viewModel.onAttendancePermissionResult(
                session.copy(coordinates = null),
                hasPrecisePermission = true,
                hasApproximatePermission = true,
            )
            assertEquals(HomeAttendanceLocationIssue.SessionUnavailable, viewModel.uiState.value.locationIssue)
            assertEquals("1234", viewModel.uiState.value.code)
            advanceUntilIdle()
            assertEquals(0, measurements)
            assertEquals(0, submissions)

            assertTrue(viewModel.confirmAttendance(session, hasPrecisePermission = false))
            viewModel.onAttendancePermissionResult(
                session,
                hasPrecisePermission = true,
                hasApproximatePermission = true,
            )
            advanceUntilIdle()
            assertEquals(1, measurements)
            assertEquals(1, submissions)
        }

    @Test
    fun blockedDistancePreservesCodeAndRetryMeasuresAgainBeforeSending() =
        runTest {
            var measurements = 0
            val submissions = mutableListOf<Pair<Long, Int>>()
            val distanceRepository =
                preciseRepository { target ->
                    assertEquals(session.coordinates, target)
                    if (++measurements == 1) allowedDistance.copy(meters = 100.01f) else allowedDistance
                }
            val viewModel = viewModel(distanceRepository) { sessionId, code -> submissions += sessionId to code }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("0123")
            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            advanceUntilIdle()
            assertEquals(HomeAttendanceLocationIssue.OutOfRange, viewModel.uiState.value.locationIssue)
            assertEquals("0123", viewModel.uiState.value.code)
            assertTrue(submissions.isEmpty())

            viewModel.confirmAttendance(session, hasPrecisePermission = true)
            advanceUntilIdle()
            assertEquals(2, measurements)
            assertEquals(listOf(session.sessionId to 123), submissions)
        }

    @Test
    fun movingAwayFromTheScreenCancelsLocationAndPreventsLateSubmission() =
        runTest(mainDispatcherRule.dispatcher) {
            val measurement = CompletableDeferred<AttendanceLocationReading>()
            var measurements = 0
            var submissions = 0
            val distanceRepository =
                preciseRepository { _ ->
                    measurements++
                    withContext(NonCancellable) { measurement.await() }
                }
            val viewModel = viewModel(distanceRepository) { _, _ -> submissions++ }
            viewModel.openAttendanceCode(session.sessionId)
            viewModel.updateCode("1234")
            repeat(2) { viewModel.confirmAttendance(session, hasPrecisePermission = true) }
            runCurrent()
            viewModel.dismissSheet()
            viewModel.updateCode("9999")
            assertEquals(HomeSubmissionStage.CheckingLocation, viewModel.uiState.value.submissionStage)
            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertEquals(1, measurements)
            viewModel.cancelAttendanceLocation()
            measurement.complete(allowedDistance)
            advanceUntilIdle()
            assertEquals(0, submissions)
            assertEquals("1234", viewModel.uiState.value.code)
            assertFalse(viewModel.uiState.value.isSubmitting)
        }

    private fun viewModel(
        distanceRepository: SessionDistanceRepository = preciseRepository { _ -> allowedDistance },
        verify: suspend (Long, Int) -> Unit,
    ): HomeInteractionViewModel = HomeInteractionViewModel(attendanceRepository(verify), distanceRepository)

    private fun preciseRepository(
        fetch: suspend (SessionCoordinates) -> AttendanceLocationReading,
    ): SessionDistanceRepository =
        object : SessionDistanceRepository {
            override suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance =
                error("unexpected distance preview")

            override suspend fun fetchPreciseDistanceTo(coordinates: SessionCoordinates): AttendanceLocationReading =
                fetch(coordinates)
        }

    private fun attendanceRepository(verify: suspend (Long, Int) -> Unit) =
        object : AttendanceRepository {
            override suspend fun verifyAttendance(
                sessionId: Long,
                code: Int,
            ) = verify(sessionId, code)

            override suspend fun submitReport(
                type: AttendanceReportType,
                reason: String,
            ): Long = throw AssertionError("Home must not submit unsupported attendance reports")
        }
}
