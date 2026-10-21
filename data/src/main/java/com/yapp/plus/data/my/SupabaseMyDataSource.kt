package com.yapp.plus.data.my

import com.yapp.plus.data.supabase.SupabaseRestClient
import kotlinx.serialization.json.Json
import java.time.Instant

class SupabaseMyDataSource(
    private val client: SupabaseRestClient,
) : MyDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchProfile(): MyProfileData? =
        client.fetch(
            path = "mvp_my_profiles",
            parameters = listOf("select" to "display_name", "limit" to "2"),
        ) { body ->
            val rows = json.decodeFromString<List<SupabaseMyProfileResponse>>(body)
            require(rows.size <= 1)
            rows.firstOrNull()?.toData()
        }

    override suspend fun fetchAttendanceHistory(): List<MyAttendanceRecordData> =
        client.fetch(
            path = "mvp_my_attendance_history",
            parameters =
                listOf(
                    "select" to "session_id,session_name,starts_at,ends_at,checked_in_at",
                    "order" to "checked_in_at.desc,session_id.desc",
                ),
        ) { body ->
            json.decodeFromString<List<SupabaseMyAttendanceRecordResponse>>(body).map { it.toData() }
        }

    private fun SupabaseMyProfileResponse.toData(): MyProfileData {
        require(displayName.isNotBlank())
        return MyProfileData(displayName = displayName)
    }

    private fun SupabaseMyAttendanceRecordResponse.toData(): MyAttendanceRecordData {
        val startsAt = Instant.parse(this.startsAt)
        val endsAt = Instant.parse(this.endsAt)
        val checkedInAt = Instant.parse(this.checkedInAt)
        require(sessionId > 0 && sessionName.isNotBlank() && startsAt < endsAt)
        return MyAttendanceRecordData(
            sessionId = sessionId,
            sessionName = sessionName,
            startsAt = startsAt,
            endsAt = endsAt,
            checkedInAt = checkedInAt,
        )
    }
}
