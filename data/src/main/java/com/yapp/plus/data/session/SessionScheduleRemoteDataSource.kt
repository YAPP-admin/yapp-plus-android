package com.yapp.plus.data.session

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SessionScheduleRemoteDataSource(
    private val httpClient: HttpClient,
) : SessionScheduleDataSource {
    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData {
        val response =
            fetchSession(CURRENT_WEEK_SESSIONS_PATH)
                .body<CurrentWeekScheduleResponse>()
        return CurrentWeekScheduleMapper.toData(response)
    }

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData {
        val response =
            fetchSession(
                CURRENT_GENERATION_SESSIONS_PATH,
            ).body<CurrentGenerationScheduleResponse>()
        return CurrentGenerationScheduleMapper.toData(response)
    }

    private suspend fun fetchSession(
        path: String,
    ): io.ktor.client.statement.HttpResponse =
        try {
            httpClient.get(path)
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

    private companion object {
        const val CURRENT_WEEK_SESSIONS_PATH = "sessions/current-week"
        const val CURRENT_GENERATION_SESSIONS_PATH = "generations/current/sessions"
    }
}
