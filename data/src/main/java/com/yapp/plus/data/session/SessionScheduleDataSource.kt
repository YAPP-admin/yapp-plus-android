package com.yapp.plus.data.session

interface SessionScheduleDataSource {
    suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData

    suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData
}
