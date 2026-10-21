package com.yapp.plus.data.profile

import com.yapp.plus.data.supabase.SupabaseDataSourceException
import com.yapp.plus.data.supabase.SupabaseTestFixture
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseProfileDataSourceTest {
    @Test
    fun fetchesAuthenticatedProfileAndMapsValues() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals(PROFILE_PATH, request.url.encodedPath)
                assertEquals("display_name,phone_number", request.url.parameters["select"])
                assertEquals("2", request.url.parameters["limit"])
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                respond("""[{"display_name":"홍길동","phone_number":"01012345678"}]""")
            }).use { fixture ->
                assertEquals(
                    ProfileData("홍길동", "01012345678"),
                    SupabaseProfileDataSource(fixture.client).fetchProfile(),
                )
            }
        }

    @Test
    fun missingProfileReturnsNull() =
        runTest {
            SupabaseTestFixture(handler = { respond("[]") }).use { fixture ->
                assertNull(SupabaseProfileDataSource(fixture.client).fetchProfile())
            }
        }

    @Test
    fun upsertUsesAuthenticatedCallerAndIsSafeToRetry() =
        runTest {
            var requestCount = 0
            SupabaseTestFixture(handler = { request ->
                requestCount++
                assertEquals(HttpMethod.Post, request.method)
                assertEquals(PROFILE_PATH, request.url.encodedPath)
                assertEquals("user_id", request.url.parameters["on_conflict"])
                assertEquals("sb_publishable_test", request.headers["apikey"])
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                assertEquals("resolution=merge-duplicates,return=minimal", request.headers["Prefer"])
                assertEquals(ContentType.Application.Json, request.body.contentType)
                val body = Json.parseToJsonElement((request.body as TextContent).text)
                assertEquals(
                    Json.parseToJsonElement(
                        """{"display_name":"홍길동","phone_number":"01012345678"}""",
                    ),
                    body,
                )
                respond("")
            }).use { fixture ->
                val source = SupabaseProfileDataSource(fixture.client)
                val profile = ProfileData("홍길동", "01012345678")
                source.saveProfile(profile)
                source.saveProfile(profile)
            }
            assertEquals(2, requestCount)
        }

    @Test
    fun malformedOrDuplicateRowsFailWithoutExposingResponseContent() =
        runTest {
            val responses =
                listOf(
                    "{}",
                    """[{"display_name":"","phone_number":"01012345678"}]""",
                    """[{"display_name":"이름","phone_number":" "}]""",
                    """[{"display_name":"이름","phone_number":"01012345678"},
                        {"display_name":"이름","phone_number":"01012345678"}]""",
                )
            for (body in responses) {
                SupabaseTestFixture(handler = { respond(body) }).use { fixture ->
                    val error =
                        runCatching {
                            SupabaseProfileDataSource(fixture.client).fetchProfile()
                        }.exceptionOrNull()
                    assertTrue(error is SupabaseDataSourceException)
                    assertNull(error?.cause)
                    assertTrue(error?.message?.contains("01012345678") != true)
                }
            }
        }

    private companion object {
        const val PROFILE_PATH = "/rest/v1/mvp_my_profiles"
    }
}
