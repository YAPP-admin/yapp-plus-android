package com.yapp.plus.data.attendance

import com.yapp.plus.data.supabase.SupabaseDataSourceException
import com.yapp.plus.data.supabase.SupabaseRestClient
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant

class SupabaseAttendanceDataSource(
    private val client: SupabaseRestClient,
) : AttendanceDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    ) {
        currentCoroutineContext().ensureActive()
        if (code !in 0..MAX_CODE) throw AttendanceException(isInvalidCode = true)
        try {
            require(sessionId > 0)
            val request = buildVerificationRequest(sessionId, code)
            client.post("rpc/mvp_verify_attendance", request) { validateResponse(it, sessionId) }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            currentCoroutineContext().ensureActive()
            throw AttendanceException(
                isInvalidCode =
                    (exception as? SupabaseDataSourceException)?.code == INVALID_ATTENDANCE_CODE,
            )
        }
    }

    override suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ): Long {
        currentCoroutineContext().ensureActive()
        // The MVP migration provides no report backend.
        throw AttendanceException(isInvalidCode = false)
    }

    private fun buildVerificationRequest(
        sessionId: Long,
        code: Int,
    ): JsonObject =
        buildJsonObject {
            put("p_session_id", sessionId)
            put("p_code", code.toString().padStart(CODE_LENGTH, '0'))
        }

    private fun validateResponse(
        body: String,
        sessionId: Long,
    ) {
        val result = json.decodeFromString<List<SupabaseVerifyAttendanceResponse>>(body).single()
        require(result.sessionId == sessionId && result.verified)
        Instant.parse(result.checkedInAt)
    }

    private companion object {
        const val CODE_LENGTH = 4
        const val MAX_CODE = 9999
        const val INVALID_ATTENDANCE_CODE = "INVALID_ATTENDANCE_CODE"
    }
}
