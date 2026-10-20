package com.yapp.plus.data.session

import com.yapp.plus.data.supabase.SupabaseRestClient
import com.yapp.plus.domain.session.AttendanceStatus
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.ZoneId

class SupabaseSessionScheduleDataSource(
    private val client: SupabaseRestClient,
) : SessionScheduleDataSource {
    private val json = Json { ignoreUnknownKeys = true }
    private val zone = ZoneId.of("Asia/Seoul")
    private val sessionOrder =
        listOf("is_test" to "eq.true", "order" to "starts_at.asc,ends_at.asc,id.asc")

    override suspend fun fetchCurrentWeekSchedule(): CurrentWeekScheduleData {
        val context = fetchContext()
        val session = fetchWeekSession(context)
        return CurrentWeekScheduleData(context.weekStart, context.weekEnd, session)
    }

    private suspend fun fetchWeekSession(dates: SupabaseContextResponse): CurrentWeekSessionData? =
        client.fetch("mvp_session_schedule", weekParameters(dates)) { body ->
            val rows = json.decodeFromString<List<SupabaseSessionResponse>>(body)
            require(rows.size <= 1)
            rows.firstOrNull()?.let(::toCurrentWeekSession)
        }

    private fun weekParameters(context: SupabaseContextResponse): List<Pair<String, String>> {
        val nextWeekStart =
            context.weekEnd
                .plusDays(1)
                .atStartOfDay(zone)
                .toInstant()
        return sessionOrder +
            listOf(
                "starts_at" to "gte.${context.weekStart.atStartOfDay(zone).toInstant()}",
                "starts_at" to "lt.$nextWeekStart",
                "limit" to "1",
            )
    }

    override suspend fun fetchCurrentGenerationSchedule(): CurrentGenerationScheduleData {
        val context = fetchContext()
        val sessions =
            client.fetch("mvp_session_schedule", sessionOrder) { body ->
                json.decodeFromString<List<SupabaseSessionResponse>>(body).map { it.toData(zone) }
            }
        // Zero is test-only missing metadata, never an official YAPP generation.
        return CurrentGenerationScheduleData(
            generationId = context.generationId ?: 0,
            generationNumber = context.generationNumber ?: 0,
            sessions = sessions,
        )
    }

    private suspend fun fetchContext(): SupabaseContextResponse =
        client.fetch("mvp_context") { body ->
            json.decodeFromString<List<SupabaseContextResponse>>(body).single().also {
                require(it.timeZone == zone.id && it.weekStart.dayOfWeek == DayOfWeek.SUNDAY)
                require(it.weekEnd == it.weekStart.plusDays(6))
                require((it.generationId == null) == (it.generationNumber == null))
                require(it.generationId == null || it.generationId > 0)
                require(it.generationNumber == null || it.generationNumber > 0)
            }
        }

    private fun toCurrentWeekSession(row: SupabaseSessionResponse): CurrentWeekSessionData {
        val session = row.toData(zone)
        return CurrentWeekSessionData(
            sessionId = session.sessionId,
            name = session.name,
            date = session.date,
            startTime = session.startTime,
            location = session.location,
            attendanceStatus = AttendanceStatus.valueOf(row.attendanceStatus),
        )
    }
}
