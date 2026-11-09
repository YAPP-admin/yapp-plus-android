package com.yapp.plus.data.profile

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserMembership
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.profile.UserProfile
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileApiTest {
    @Test
    fun usesAuthenticatedProfileEndpointAndMapsMembership() =
        runTest {
            val profile =
                fetchProfile(
                    """
                    {"userId":1,"name":"회원","membershipStatus":"OB","generationId":12,
                     "generationNumber":29,"position":"MOBILE"}
                    """.trimIndent(),
                )
            assertEquals(
                UserProfile(1, "회원", UserMembership(MembershipStatus.OB, 12, 29, UserPosition.MOBILE)),
                profile,
            )
        }

    @Test
    fun acceptsNoMembershipAndPreservesUnknownEnums() =
        runTest {
            assertEquals(
                UserProfile(1, "회원", null),
                fetchProfile(
                    """
                    {"userId":1,"name":"회원","membershipStatus":null,"generationId":null,
                     "generationNumber":null,"position":null}
                    """.trimIndent(),
                ),
            )
            assertEquals(
                UserMembership(MembershipStatus.UNKNOWN, 12, 29, UserPosition.UNKNOWN),
                fetchProfile(
                    """
                    {"userId":1,"name":"회원","membershipStatus":"NEW_STATUS","generationId":12,
                     "generationNumber":29,"position":"NEW_POSITION"}
                    """.trimIndent(),
                ).membership,
            )
        }

    @Test(expected = ClientRequestException::class)
    fun activeGenerationConflictIsARequestFailure() =
        runTest {
            fetchProfile("""{"code":"ACTIVE_GENERATION_CONFLICT"}""", HttpStatusCode.Conflict)
        }

    private suspend fun fetchProfile(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): UserProfile =
        apiTestClient(
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals("/api/users/me", request.url.encodedPath)
                assertEquals("Bearer test-access", request.headers[HttpHeaders.Authorization])
                respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
            },
        ).use { client ->
            UserProfileReadRepositoryImpl(UserProfileRemoteDataSource(client)).fetchProfile()
        }
}
