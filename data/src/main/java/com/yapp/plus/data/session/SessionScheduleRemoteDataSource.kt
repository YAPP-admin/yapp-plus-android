package com.yapp.plus.data.session

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class SessionScheduleRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleResponse =
        httpClient.get(CURRENT_WEEK_SESSIONS_PATH).body()

    suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleResponse =
        httpClient.get(CURRENT_GENERATION_SESSIONS_PATH).body()

    private companion object {
        const val CURRENT_WEEK_SESSIONS_PATH = "sessions/current-week"
        const val CURRENT_GENERATION_SESSIONS_PATH = "generations/current/sessions"
    }
}
