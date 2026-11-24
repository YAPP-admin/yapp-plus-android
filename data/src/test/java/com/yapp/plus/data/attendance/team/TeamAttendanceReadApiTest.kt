package com.yapp.plus.data.attendance.team

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.attendance.TeamAttendanceMember
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.attendance.TeamAttendanceRoster
import com.yapp.plus.domain.profile.UserPosition
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalTime

class TeamAttendanceReadApiTest {
    @Test
    fun `roster maps the current membership by self flag and preserves missing position`() =
        runTest {
            val body = """
                {"teamId":7,"teamName":"7팀","members":[
                {"membershipId":10,"name":"팀원","position":null,"isMe":false},
                {"membershipId":11,"name":"본인","position":"ANDROID","isMe":true}]}
            """.trimIndent()
            client("team-members", body).use {
                val roster = repository(it).fetchTeamRoster(SESSION_ID)
                val members = listOf(
                    TeamAttendanceMember(10, "팀원", null),
                    TeamAttendanceMember(11, "본인", UserPosition.ANDROID),
                )
                assertEquals(TeamAttendanceRoster(7, "7팀", 11, members), roster)
            }
        }

    @Test
    fun `roster rejects missing self multiple selves and duplicate membership IDs`() =
        runTest {
            val member = """{"membershipId":10,"name":"본인","position":null,"isMe":true}"""
            val invalidLists = listOf(
                member.replace("true", "false"),
                "$member,${member.replace("10", "11")}",
                "$member,${member.replace("true", "false")}",
            )
            for (members in invalidLists) {
                val body = """{"teamId":7,"teamName":"7팀","members":[$members]}"""
                client("team-members", body).use {
                    val result = runCatching { repository(it).fetchTeamRoster(SESSION_ID) }
                    assertTrue(result.isFailure)
                }
            }
        }

    @Test
    fun `latest photo maps signed image expiry and distinct uploaded and submitted times`() =
        runTest {
            val url = "https://storage.test/photo?signature=fixture"
            val expiry = "2026-11-23T02:05:00Z"
            val body = photoBody("PENDING", "\"$url\"", "\"$expiry\"")
            client("team-attendance-photos", body).use {
                val photo = requireNotNull(repository(it).fetchLatestPhoto(SESSION_ID))
                assertEquals(TeamAttendancePhotoStatus.PENDING, photo.status)
                assertEquals(TeamAttendancePhotoImage(url, Instant.parse(expiry)), photo.image)
                assertEquals(Instant.parse("2026-11-23T01:59:00Z"), photo.uploadedAt)
                assertEquals(LocalTime.of(10, 59), photo.uploadedTime)
                assertEquals(Instant.parse("2026-11-23T02:00:00Z"), photo.submittedAt)
                assertEquals(4, photo.selectedMemberCount)
                assertNull(photo.uploaderName)
            }
        }

    @Test
    fun `latest approved or rejected submission retains metadata without a usable image`() =
        runTest {
            val expiry = "\"2026-11-23T02:05:00Z\""
            val bodies =
                mapOf(
                    TeamAttendancePhotoStatus.APPROVED to photoBody("APPROVED", "null", "null"),
                    TeamAttendancePhotoStatus.REJECTED to
                        photoBody("REJECTED", "\"http://storage.test/photo\"", expiry),
                )
            for ((status, body) in bodies) {
                client("team-attendance-photos", body).use {
                    val photo = requireNotNull(repository(it).fetchLatestPhoto(SESSION_ID))
                    assertEquals(status, photo.status)
                    assertEquals(4, photo.selectedMemberCount)
                    assertNull(photo.image)
                }
            }
        }

    @Test
    fun `only a successful null photo maps to no submission`() =
        runTest {
            val path = "team-attendance-photos"
            client(path, """{"photo":null}""").use {
                assertNull(repository(it).fetchLatestPhoto(SESSION_ID))
            }
            client(path, "{}", HttpStatusCode.Forbidden).use {
                val failure =
                    runCatching { repository(it).fetchLatestPhoto(SESSION_ID) }.exceptionOrNull()
                val status = (failure as ResponseException).response.status
                assertEquals(HttpStatusCode.Forbidden, status)
            }
        }

    private fun client(
        path: String,
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = apiTestClient(
        MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/api/sessions/$SESSION_ID/$path", request.url.encodedPath)
            assertEquals("Bearer test-access", request.headers[HttpHeaders.Authorization])
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        },
    )

    private fun repository(client: HttpClient) =
        TeamAttendanceReadRepositoryImpl(TeamAttendanceReadRemoteDataSource(client))

    private fun photoBody(
        status: String,
        url: String,
        expiry: String,
    ): String = """
        {"photo":{"photoId":1,"teamId":7,"teamName":"7팀","status":"$status",
        "imageUrl":$url,"imageUrlExpiresAt":$expiry,"uploadedAt":"2026-11-23T01:59:00Z",
        "uploadedTime":"10:59","submittedAt":"2026-11-23T02:00:00Z",
        "uploader":{"userId":1,"name":null},"selectedMemberCount":4}}
    """.trimIndent()

    private companion object {
        const val SESSION_ID = 42L
    }
}
