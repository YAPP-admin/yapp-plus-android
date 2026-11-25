package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.SessionScheduleRepository
import kotlinx.coroutines.CompletableDeferred
import java.time.LocalDate
import java.time.LocalTime

internal class FakeSessionScheduleRepository : SessionScheduleRepository {
    var currentWeekFailure: ScheduleFailure? = null
    var currentWeekRequestCount = 0
    var gate: CompletableDeferred<Unit>? = null
    var attendanceStatus = AttendanceStatus.NOT_OPEN

    override suspend fun fetchCurrentWeekSchedule(): YappResult<
        CurrentWeekSchedule,
        ScheduleFailure,
    > {
        currentWeekRequestCount += 1
        gate?.await()
        currentWeekFailure?.let { return YappResult.Failure(it) }
        val schedule =
            CurrentWeekSchedule(
                weekStartDate = LocalDate.of(2026, 10, 18),
                weekEndDate = LocalDate.of(2026, 10, 24),
                session =
                    CurrentWeekSession(
                        sessionId = 1,
                        name = "개발 세션",
                        date = LocalDate.of(2026, 10, 24),
                        startTime = LocalTime.of(14, 0),
                        location = "DMC 첨단산업센터",
                        attendanceStatus = attendanceStatus,
                    ),
            )
        return YappResult.Success(schedule)
    }

    override suspend fun fetchCurrentGenerationSchedule() =
        YappResult.Success(
            CurrentGenerationSchedule(
                generationId = 19,
                generationNumber = 19,
                sessions = emptyList(),
            ),
        )
}
