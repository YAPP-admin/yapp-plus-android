package com.yapp.plus.data.session

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.SessionScheduleRepository
import io.ktor.client.HttpClient

class SessionScheduleRepositoryImpl(
    httpClient: HttpClient,
) : SessionScheduleRepository {
    private val remoteDataSource = SessionScheduleRemoteDataSource(httpClient)

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekSchedule =
        CurrentWeekScheduleMapper.toDomain(remoteDataSource.fetchCurrentWeekSchedule())

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationSchedule =
        CurrentGenerationScheduleMapper.toDomain(remoteDataSource.fetchCurrentGenerationSchedule())
}
