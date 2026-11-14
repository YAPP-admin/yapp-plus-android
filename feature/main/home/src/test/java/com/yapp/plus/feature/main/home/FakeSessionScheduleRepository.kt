package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import kotlinx.coroutines.CompletableDeferred
import java.time.LocalDate
import java.time.LocalTime

internal class FakeSessionScheduleRepository : SessionScheduleRepository {
    var currentWeekError: Exception? = null
    var currentWeekRequestCount = 0
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule {
        currentWeekRequestCount += 1
        gate?.await()
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
