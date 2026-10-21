package com.yapp.plus.data.my

import com.yapp.plus.data.supabase.SupabaseDataSourceException
import com.yapp.plus.data.supabase.SupabaseTestFixture
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class SupabaseMyDataSourceTest {
    @Test
    fun fetchesAuthenticatedProfileAndMapsDisplayName() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals(PROFILE_PATH, request.url.encodedPath)
                assertEquals("display_name", request.url.parameters["select"])
                assertEquals("2", request.url.parameters["limit"])
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                respond("""[{"display_name":"홍길동"}]""")
            }).use { fixture ->
                assertEquals(
                    MyProfileData("홍길동"),
                    SupabaseMyDataSource(fixture.client).fetchProfile(),
                )
            }
        }

    @Test
    fun missingProfileReturnsNull() =
        runTest {
            SupabaseTestFixture(handler = { respond("[]") }).use { fixture ->
                assertNull(SupabaseMyDataSource(fixture.client).fetchProfile())
            }
        }

    @Test
    fun fetchesAuthenticatedHistoryInNewestFirstOrderAndMapsTimes() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals(HISTORY_PATH, request.url.encodedPath)
                assertEquals(
                    "session_id,session_name,starts_at,ends_at,checked_in_at",
                    request.url.parameters["select"],
                )
                assertEquals("checked_in_at.desc,session_id.desc", request.url.parameters["order"])
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                respond("""[$ATTENDANCE_RECORD]""")
            }).use { fixture ->
                assertEquals(
                    listOf(
                        MyAttendanceRecordData(
                            sessionId = 42,
                            sessionName = "테스트 세션",
                            startsAt = Instant.parse("2026-10-24T05:00:00Z"),
                            endsAt = Instant.parse("2026-10-24T09:00:00Z"),
                            checkedInAt = Instant.parse("2026-10-24T04:45:00Z"),
                        ),
                    ),
                    SupabaseMyDataSource(fixture.client).fetchAttendanceHistory(),
                )
            }
        }

    @Test
    fun emptyHistoryReturnsEmptyList() =
        runTest {
            SupabaseTestFixture(handler = { respond("[]") }).use { fixture ->
                assertTrue(SupabaseMyDataSource(fixture.client).fetchAttendanceHistory().isEmpty())
            }
        }

    @Test
    fun malformedProfileAndHistoryFailWithoutExposingResponseContent() =
        runTest {
            val invalidProfiles =
                listOf(
                    "{}",
                    """[{"display_name":" "}]""",
                    """[{"display_name":"홍길동"},{"display_name":"홍길동"}]""",
                )
            for (body in invalidProfiles) {
                SupabaseTestFixture(handler = { respond(body) }).use { fixture ->
                    val error = runCatching { SupabaseMyDataSource(fixture.client).fetchProfile() }.exceptionOrNull()
                    assertTrue(error is SupabaseDataSourceException)
                    assertNull(error?.cause)
                    assertTrue(error?.message?.contains("홍길동") != true)
                }
            }

            val invalidHistory =
                listOf(
                    "{}",
                    """[${ATTENDANCE_RECORD.replace("2026-10-24T05:00:00Z", "invalid")}]""",
                    """[${ATTENDANCE_RECORD.replace("42", "0")}]""",
                    """[${ATTENDANCE_RECORD.replace("2026-10-24T09:00:00Z", "2026-10-24T04:00:00Z")}]""",
                )
            for (body in invalidHistory) {
                SupabaseTestFixture(handler = { respond(body) }).use { fixture ->
                    val error =
                        runCatching { SupabaseMyDataSource(fixture.client).fetchAttendanceHistory() }
                            .exceptionOrNull()
                    assertTrue(error is SupabaseDataSourceException)
                    assertNull(error?.cause)
                }
            }
        }

    private companion object {
        const val PROFILE_PATH = "/rest/v1/mvp_my_profiles"
        const val HISTORY_PATH = "/rest/v1/mvp_my_attendance_history"
        const val ATTENDANCE_RECORD =
            """{"session_id":42,"session_name":"테스트 세션","starts_at":"2026-10-24T05:00:00Z",
                "ends_at":"2026-10-24T09:00:00Z","checked_in_at":"2026-10-24T04:45:00Z"}"""
    }
}
