package com.yapp.plus.data.session

class FakeSessionScheduleDataSource : SessionScheduleDataSource {
    var currentWeekSchedule: CurrentWeekScheduleData? = null
    var currentGenerationSchedule: CurrentGenerationScheduleData? = null
    var failure: Throwable? = null

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData {
        failure?.let { throw it }
        return checkNotNull(currentWeekSchedule)
    }

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData {
        failure?.let { throw it }
        return checkNotNull(currentGenerationSchedule)
    }
}
