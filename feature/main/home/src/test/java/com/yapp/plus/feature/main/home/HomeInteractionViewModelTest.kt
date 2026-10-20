package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.feature.main.home.model.HomeSheet
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

    @Test
    fun attendanceUsesVerificationOnlyAndBlocksChangesUntilServerCompletion() =
        runTest {
            val response = CompletableDeferred<Unit>()
            val requests = mutableListOf<Pair<Long, Int>>()
            val viewModel =
                HomeInteractionViewModel(
                    attendanceRepository { sessionId, code ->
                        requests += sessionId to code
                        response.await()
                    },
                )
            viewModel.openAttendanceCode()
            viewModel.updateCode("0a1２239")
            viewModel.verifyAttendance(42)

            assertTrue(viewModel.uiState.value.isSubmitting)
            assertFalse(viewModel.uiState.value.isAttendanceComplete)
            viewModel.verifyAttendance(42)
            viewModel.dismissSheet()
            viewModel.updateCode("9999")
            viewModel.openAttendanceCode()
            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertEquals("0123", viewModel.uiState.value.code)

            response.complete(Unit)
            advanceUntilIdle()

            assertEquals(listOf(42L to 123), requests)
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            assertEquals(42L, viewModel.uiState.value.attendedSessionId)
            assertTrue(viewModel.uiState.value.isAttendanceComplete)
            assertFalse(viewModel.uiState.value.isSubmitting)
            viewModel.acknowledgeAttendanceComplete()
            assertFalse(viewModel.uiState.value.isAttendanceComplete)
            assertEquals(42L, viewModel.uiState.value.attendedSessionId)
        }

    @Test
    fun invalidSessionAndIncompleteCodeDoNotSubmitAndReopeningClearsInput() =
        runTest {
            var requests = 0
            val viewModel =
                HomeInteractionViewModel(attendanceRepository { _, _ -> requests += 1 })
            viewModel.openAttendanceCode()
            viewModel.updateCode("12")
            viewModel.verifyAttendance(0)
            assertNull(viewModel.uiState.value.errorResource)

            viewModel.verifyAttendance(42)
            assertEquals(R.string.home_code_required, viewModel.uiState.value.errorResource)
            assertTrue(viewModel.uiState.value.isInvalidCode)
            assertEquals("12", viewModel.uiState.value.code)
            viewModel.dismissSheet()
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            viewModel.openAttendanceCode()

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
                HomeInteractionViewModel(
                    attendanceRepository { _, _ ->
                        when (++requests) {
                            1 -> throw AttendanceException(isInvalidCode = true)
                            2 -> error("provider failure")
                        }
                    },
                )
            viewModel.openAttendanceCode()
            viewModel.updateCode("1234")
            viewModel.verifyAttendance(42)
            advanceUntilIdle()

            assertEquals(R.string.home_code_invalid, viewModel.uiState.value.errorResource)
            assertTrue(viewModel.uiState.value.isInvalidCode)
            assertEquals("1234", viewModel.uiState.value.code)
            assertFalse(viewModel.uiState.value.isAttendanceComplete)
            viewModel.updateCode("2345")
            assertNull(viewModel.uiState.value.errorResource)
            assertFalse(viewModel.uiState.value.isInvalidCode)
            viewModel.verifyAttendance(42)
            advanceUntilIdle()

            assertEquals(R.string.home_submit_error, viewModel.uiState.value.errorResource)
            assertFalse(viewModel.uiState.value.isInvalidCode)
            assertEquals("2345", viewModel.uiState.value.code)
            assertEquals(HomeSheet.AttendanceCode, viewModel.uiState.value.sheet)
            assertFalse(viewModel.uiState.value.isAttendanceComplete)
            viewModel.verifyAttendance(42)
            advanceUntilIdle()

            assertEquals(3, requests)
            assertTrue(viewModel.uiState.value.isAttendanceComplete)
            assertNull(viewModel.uiState.value.errorResource)
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
