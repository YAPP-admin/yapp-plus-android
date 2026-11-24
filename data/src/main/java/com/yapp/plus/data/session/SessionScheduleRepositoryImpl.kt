package com.yapp.plus.data.session

import com.yapp.plus.core.network.NetworkRequestResult
import com.yapp.plus.core.network.invokeRequest
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.NoActiveGenerationException
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.SessionScheduleRepository

class SessionScheduleRepositoryImpl(
    private val dataSource: SessionScheduleDataSource,
) : SessionScheduleRepository {
    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule =
        CurrentWeekScheduleMapper.toDomain(dataSource.fetchCurrentWeekSchedule())

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationSchedule =
        CurrentGenerationScheduleMapper.toDomain(dataSource.fetchCurrentGenerationSchedule())

    internal suspend fun fetchWeekResult() = fetchSchedule(::fetchCurrentWeekSchedule)

    internal suspend fun fetchGenerationResult() = fetchSchedule(::fetchCurrentGenerationSchedule)

    private suspend fun <T> fetchSchedule(fetch: suspend () -> T): YappResult<T, ScheduleFailure> {
        val result =
            invokeRequest<YappResult<T, ScheduleFailure>> {
                try {
                    YappResult.Success(fetch())
                } catch (_: NoActiveGenerationException) {
                    YappResult.Failure(ScheduleFailure.NoActiveGeneration)
                }
            }
        return when (result) {
            is NetworkRequestResult.Success -> result.data
            is NetworkRequestResult.Error -> YappResult.Failure(ScheduleFailure.Unavailable)
        }
    }
}
