package com.yapp.plus.domain.session

import com.yapp.plus.domain.result.YappResult

interface SessionScheduleRepository {
    suspend fun fetchCurrentWeekSchedule(): YappResult<CurrentWeekSchedule, ScheduleFailure>

    suspend fun fetchCurrentGenerationSchedule(): YappResult<
        CurrentGenerationSchedule,
        ScheduleFailure,
    >
}
