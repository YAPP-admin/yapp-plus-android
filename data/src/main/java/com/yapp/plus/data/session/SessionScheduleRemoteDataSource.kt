package com.yapp.plus.data.session

import com.yapp.plus.core.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SessionScheduleRemoteDataSource(
    private val httpClient: HttpClient,
) : SessionScheduleDataSource {
    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData {
        val response =
            fetchSession(CURRENT_WEEK_SESSIONS_PATH, BuildConfig.CURRENT_WEEK_MOCK_EXAMPLE)
                .body<CurrentWeekScheduleResponse>()
        return CurrentWeekScheduleMapper.toData(response)
    }

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData {
        val response =
            fetchSession(
                CURRENT_GENERATION_SESSIONS_PATH,
                BuildConfig.CURRENT_GENERATION_SESSIONS_MOCK_EXAMPLE,
            ).body<CurrentGenerationScheduleResponse>()
        return CurrentGenerationScheduleMapper.toData(response)
    }

    private suspend fun fetchSession(
        path: String,
        example: String,
    ): io.ktor.client.statement.HttpResponse =
        try {
            httpClient.get(path) { addMockExample(example) }
        } catch (exception: ClientRequestException) {
            val body = exception.response.bodyAsText()
            val code =
                runCatching {
                    Json
                        .parseToJsonElement(body)
                        .jsonObject["code"]
                        ?.jsonPrimitive
                        ?.content
                }.getOrNull()
            if (exception.response.status == HttpStatusCode.NotFound &&
                code == "CURRENT_GENERATION_NOT_FOUND"
            ) {
                throw NoActiveGenerationException()
            }
            throw exception
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
