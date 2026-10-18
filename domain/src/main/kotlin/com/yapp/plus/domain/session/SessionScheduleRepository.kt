package com.yapp.plus.domain.session

interface SessionScheduleRepository {
    suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule

    suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationSchedule
}
