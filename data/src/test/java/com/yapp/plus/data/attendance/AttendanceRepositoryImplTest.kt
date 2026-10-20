package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AttendanceRepositoryImplTest {
    @Test
    fun `verification forwards the session and numeric code to injected source`() =
        runTest {
            val source = FakeAttendanceDataSource()
            AttendanceRepositoryImpl(source).verifyAttendance(sessionId = 42, code = 123)
            assertEquals(42L, source.verifiedSessionId)
            assertEquals(123, source.verifiedCode)
        }

    @Test
    fun `report preserves each report type and reason and returns confirmed session`() =
        runTest {
            val source = FakeAttendanceDataSource().apply { reportSessionId = 42 }
            val repository = AttendanceRepositoryImpl(source)
            for (type in AttendanceReportType.entries) {
                assertEquals(42L, repository.submitReport(type = type, reason = "사전 보고 사유"))
                assertEquals(type, source.reportType)
                assertEquals("사전 보고 사유", source.reportReason)
            }
        }

    @Test
    fun `source attendance errors and cancellation propagate unchanged`() =
        runTest {
            val source = FakeAttendanceDataSource()
            val repository = AttendanceRepositoryImpl(source)
            val failures =
                listOf(
                    AttendanceException(isInvalidCode = true),
                    AttendanceException(isInvalidCode = false),
                    CancellationException(),
                )
            for (failure in failures) {
                source.failure = failure
                assertSame(
                    failure,
                    runCatching {
                        repository.verifyAttendance(sessionId = 42, code = 1234)
                    }.exceptionOrNull(),
                )
                assertSame(
                    failure,
                    runCatching {
                        repository.submitReport(type = AttendanceReportType.ABSENT, reason = "사유")
                    }.exceptionOrNull(),
                )
            }
        }
}
