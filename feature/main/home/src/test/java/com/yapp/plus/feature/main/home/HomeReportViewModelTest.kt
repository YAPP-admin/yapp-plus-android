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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeReportViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun reportSelectsTypeThenReasonAndPreventsDuplicateSubmission() =
        runTest {
            val response = CompletableDeferred<Long>()
            val requests = mutableListOf<Pair<AttendanceReportType, String>>()
            val viewModel =
                HomeInteractionViewModel(
                    repository { type, reason ->
                        requests += type to reason
                        response.await()
                    },
                )
            viewModel.openReport()
            viewModel.confirmReport()
            assertEquals(HomeSheet.ReportType, viewModel.uiState.value.sheet)
            viewModel.selectReportType(AttendanceReportType.EARLY_LEAVE)
            viewModel.confirmReport()
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
            viewModel.updateReportReason("개인 일정")
            viewModel.confirmReport()
            viewModel.confirmReport()
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
                HomeInteractionViewModel(
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
            viewModel.confirmReport()
            viewModel.updateReportReason("가".repeat(29) + "😀")
            assertEquals("가".repeat(29), viewModel.uiState.value.reportReason)
            viewModel.confirmReport()
            assertEquals(
                R.string.home_report_session_changed,
                viewModel.uiState.value.errorResource,
            )
            assertEquals(HomeSheet.ReportReason, viewModel.uiState.value.sheet)
            assertFalse(viewModel.uiState.value.isReportComplete)
            fails = false
            viewModel.confirmReport()
            assertTrue(viewModel.uiState.value.isReportComplete)
        }

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
