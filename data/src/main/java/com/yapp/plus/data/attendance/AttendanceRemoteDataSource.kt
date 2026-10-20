package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.Instant

internal class AttendanceRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    ) {
        val response = post("attendance-verifications", VerifyAttendanceRequest(sessionId, code))
        val result = response.body<VerifyAttendanceResponse>()
        check(result.verified) { "Attendance verification was not confirmed" }
        Instant.parse(result.checkedInAt)
    }

    suspend fun submitReport(
        type: String,
        reason: String,
    ): Long {
        val response = post("attendance-reports", SubmitAttendanceReportRequest(type, reason))
        val result = response.body<AttendanceReportResponse>()
        check(result.sessionId > 0) { "Missing attendance report confirmation" }
        Instant.parse(result.submittedAt)
        return result.sessionId
    }

    private suspend inline fun <reified T : Any> post(
        path: String,
        request: T,
    ): HttpResponse {
        val response =
            httpClient.post(path) {
                expectSuccess = false
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        if (response.status.value !in 200..299) {
            val error = response.body<AttendanceErrorResponse>()
            throw AttendanceException(isInvalidCode = error.code == "INVALID_ATTENDANCE_CODE")
        }
        return response
    }
}
