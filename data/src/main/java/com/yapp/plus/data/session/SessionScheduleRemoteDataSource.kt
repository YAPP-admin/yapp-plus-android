package com.yapp.plus.data.session

import com.yapp.plus.core.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

class SessionScheduleRemoteDataSource(
    private val httpClient: HttpClient,
) : SessionScheduleDataSource {
    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData {
        val response =
            httpClient
                .get(CURRENT_WEEK_SESSIONS_PATH) {
                    addMockExample(BuildConfig.CURRENT_WEEK_MOCK_EXAMPLE)
                }.body<CurrentWeekScheduleResponse>()
        return CurrentWeekScheduleMapper.toData(response)
    }

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData {
        val response =
            httpClient
                .get(CURRENT_GENERATION_SESSIONS_PATH) {
                    addMockExample(BuildConfig.CURRENT_GENERATION_SESSIONS_MOCK_EXAMPLE)
                }.body<CurrentGenerationScheduleResponse>()
        return CurrentGenerationScheduleMapper.toData(response)
    }

    private fun HttpRequestBuilder.addMockExample(exampleName: String) {
        if (BuildConfig.MOCK_API_ENABLED && exampleName.isNotBlank()) {
            header(HttpHeaders.Prefer, "example=$exampleName")
        }
    }

    private companion object {
        const val CURRENT_WEEK_SESSIONS_PATH = "sessions/current-week"
        const val CURRENT_GENERATION_SESSIONS_PATH = "generations/current/sessions"
    }
}
