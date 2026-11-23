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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeReportViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun requiresReasonForEveryReportTypeAndSendsTrimmedInput() =
        runTest {
            val requests = mutableListOf<Pair<AttendanceReportType, String>>()
            val viewModel =
                viewModel(
                    repository { type, reason ->
                        requests += type to reason
                        42L
                    },
                )
            val date = LocalDate.of(2026, 11, 21)
            val time = LocalTime.of(14, 0)
            val beforeStart = Instant.parse("2026-11-21T04:59:59Z")

            for (type in AttendanceReportType.entries) {
                viewModel.openReport()
                viewModel.selectReportType(type)
                viewModel.confirmReport(date, time, beforeStart)
                assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)

                val previousRequests = requests.size
                for (reason in listOf("", " \t\u3000")) {
                    viewModel.updateReportReason(reason)
                    viewModel.confirmReport(date, time, beforeStart)
                    advanceUntilIdle()
                    assertEquals(previousRequests, requests.size)
                    assertFalse(viewModel.uiState.value.isSubmitting)
                    assertEquals(reason, viewModel.uiState.value.reportReason)
                    assertEquals(
                        R.string.home_report_reason_required,
                        viewModel.uiState.value.errorResource,
                    )
                }

                viewModel.updateReportReason("  개인 일정  ")
                viewModel.confirmReport(date, time, beforeStart)
                advanceUntilIdle()
                assertEquals(previousRequests + 1, requests.size)
                assertEquals(type to "개인 일정", requests.last())
                assertTrue(viewModel.uiState.value.isReportComplete)
                viewModel.acknowledgeReportComplete()
            }
        }

    @Test fun reportSelectsTypeThenReasonAndPreventsDuplicateSubmission() =
        runTest {
            val response = CompletableDeferred<Long>()
            val requests = mutableListOf<Pair<AttendanceReportType, String>>()
            val viewModel =
                viewModel(
                    repository { type, reason ->
                        requests += type to reason
                        response.await()
                    },
                )
            viewModel.openReport()
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            assertEquals(HomeSheet.ReportType, viewModel.uiState.value.sheet)
            viewModel.selectReportType(AttendanceReportType.EARLY_LEAVE)
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
            viewModel.updateReportReason("개인 일정")
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            viewModel.dismissSheet()
            viewModel.updateReportReason("변경")
            assertTrue(viewModel.uiState.value.isSubmitting)
            assertFalse(viewModel.uiState.value.isReportComplete)
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
            response.complete(42)
            advanceUntilIdle()
            assertEquals(listOf(AttendanceReportType.EARLY_LEAVE to "개인 일정"), requests)
            assertEquals(HomeSheet.None, viewModel.uiState.value.sheet)
            assertTrue(viewModel.uiState.value.isReportComplete)
            viewModel.acknowledgeReportComplete()
            assertFalse(viewModel.uiState.value.isReportComplete)
        }

    @Test fun failurePreservesInputAndReasonMatchesServerLengthLimit() =
        runTest {
            var fails = true
            val viewModel =
                viewModel(
                    repository { _, _ ->
                        if (fails) {
                            throw AttendanceException(
                                code = "ATTENDANCE_REPORT_SESSION_CHANGED",
                            )
                        }
                        42
                    },
                )
            viewModel.openReport()
            viewModel.selectReportType(AttendanceReportType.LATE)
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            viewModel.updateReportReason("가".repeat(29) + "😀")
            assertEquals("가".repeat(29), viewModel.uiState.value.reportReason)
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            assertEquals(
                R.string.home_report_session_changed,
                viewModel.uiState.value.errorResource,
            )
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
            assertFalse(viewModel.uiState.value.isReportComplete)
            fails = false
            viewModel.confirmReport(java.time.LocalDate.of(2099, 1, 1), java.time.LocalTime.NOON)
            assertTrue(viewModel.uiState.value.isReportComplete)
        }

    @Test
    fun missingOrStartedSessionNeverSubmitsAndPreservesReason() =
        runTest {
            var requests = 0
            val viewModel =
                viewModel(
                    repository { _, _ ->
                        requests++
                        42
                    },
                )
            val date = java.time.LocalDate.of(2026, 11, 21)
            val time = java.time.LocalTime.of(14, 0)
            val start = java.time.Instant.parse("2026-11-21T05:00:00Z")
            viewModel.openReport()
            viewModel.selectReportType(AttendanceReportType.ABSENT)
            viewModel.confirmReport(date, time, start.minusSeconds(1))
            viewModel.updateReportReason("개인 사정")
            viewModel.confirmReport(null, null, start.minusSeconds(1))
            viewModel.confirmReport(date, time, start)
            advanceUntilIdle()
            assertEquals(0, requests)
            assertEquals("개인 사정", viewModel.uiState.value.reportReason)
            assertEquals(R.string.home_report_closed, viewModel.uiState.value.errorResource)
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
        }

    private fun viewModel(repository: AttendanceRepository): HomeInteractionViewModel =
        HomeInteractionViewModel(repository) { error("not location attendance") }

    private fun repository(submit: suspend (AttendanceReportType, String) -> Long) =
        object : AttendanceRepository {
            override suspend fun verifyAttendance(
                sessionId: Long,
                code: Int,
            ) = error("not verification")

            override suspend fun submitReport(
                type: AttendanceReportType,
                reason: String,
            ) = submit(type, reason)
        }
}
