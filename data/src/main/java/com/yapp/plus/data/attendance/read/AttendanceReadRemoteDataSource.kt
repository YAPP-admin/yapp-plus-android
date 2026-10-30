package com.yapp.plus.data.attendance.read

import com.yapp.plus.data.auth.model.response.ApiErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

class AttendanceReadRemoteDataSource(
    private val httpClient: HttpClient,
) : AttendanceReadDataSource {
    override suspend fun fetchScore(): AttendanceScoreData? {
        val response = httpClient.get("attendance-scores/me") { expectSuccess = false }
        if (response.status == HttpStatusCode.OK) {
            return response.body<AttendanceScoreResponse>().toData()
        }
        val code = response.body<ApiErrorResponse>().code
        if (response.status == HttpStatusCode.Forbidden &&
            code == "ATTENDANCE_SCORE_NOT_ELIGIBLE"
        ) {
            return null
        }
        if (response.status == HttpStatusCode.NotFound && code == "CURRENT_GENERATION_NOT_FOUND") {
            return null
        }
        error("Attendance score request failed: ${response.status.value}")
    }

    override suspend fun fetchHistory(cursor: String?): AttendanceHistoryData {
        val response =
            httpClient
                .get("attendance-history") {
                    cursor?.let { parameter("cursor", it) }
                }.body<AttendanceHistoryResponse>()
        return response.toData()
    }
}
