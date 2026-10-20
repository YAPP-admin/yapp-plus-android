package com.yapp.plus.data.session

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.SessionScheduleRepository

class SessionScheduleRepositoryImpl(
    private val dataSource: SessionScheduleDataSource,
) : SessionScheduleRepository {
    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule =
        CurrentWeekScheduleMapper.toDomain(dataSource.fetchCurrentWeekSchedule())

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationSchedule =
        CurrentGenerationScheduleMapper.toDomain(dataSource.fetchCurrentGenerationSchedule())
}
