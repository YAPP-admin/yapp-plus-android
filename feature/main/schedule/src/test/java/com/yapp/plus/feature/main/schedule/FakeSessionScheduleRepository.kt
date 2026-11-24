package com.yapp.plus.feature.main.schedule

import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.domain.session.SessionType
import kotlinx.coroutines.CompletableDeferred
import java.time.LocalDate
import java.time.LocalTime

internal class FakeSessionScheduleRepository : SessionScheduleRepository {
    private val date = LocalDate.now().plusDays(1)
    var generationSchedule =
        CurrentGenerationSchedule(
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
    var generationFailure: ScheduleFailure? = null
    var generationGate: CompletableDeferred<Unit>? = null
    var generationScheduleRequestCount = 0
        private set

    override suspend fun fetchCurrentWeekSchedule() =
        YappResult.Success(CurrentWeekSchedule(date, date.plusDays(6), null))

    override suspend fun fetchCurrentGenerationSchedule(): YappResult<
        CurrentGenerationSchedule,
        ScheduleFailure,
    > {
        generationScheduleRequestCount += 1
        generationGate?.await()
        generationFailure?.let { return YappResult.Failure(it) }
        return YappResult.Success(generationSchedule)
    }
}
