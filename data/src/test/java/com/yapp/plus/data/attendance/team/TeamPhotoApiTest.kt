package com.yapp.plus.data.attendance.team

import com.yapp.plus.data.apiTestClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest
import java.util.Base64

class TeamPhotoApiTest {
    @Test fun uploadUsesChecksumSignedHeadersAndNoApiCredentialsBeforeSubmitting() =
        runTest {
            val bytes = byteArrayOf(1, 2, 3, 4)
            var uploaded = false
            val api =
                apiTestClient(
                    MockEngine { request ->
                        assertEquals(
                            "Bearer test-access",
                            request.headers[HttpHeaders.Authorization],
                        )
                        val body =
                            Json
                                .parseToJsonElement(
                                    (request.body as TextContent).text,
                                ).jsonObject
                        if (request.url.encodedPath.endsWith("/presign")) {
                            assertEquals(4, body.getValue("sizeBytes").jsonPrimitive.int)
                            assertEquals(
                                Base64.getEncoder().encodeToString(
                                    MessageDigest.getInstance("SHA-256").digest(bytes),
                                ),
                                body.getValue("checksumSha256").jsonPrimitive.content,
                            )
                            respond(
                                """
{"imageKey":"team/key","uploadUrl":"https://storage.test/photo",
"signedHeaders":{"Content-Type":"image/png","x-amz-checksum-sha256":"signed"},
"expiresAt":"2099-01-01T00:00:00Z"}
""",
                                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                            )
                        } else {
                            assertTrue(uploaded)
                            assertEquals(
                                listOf(1L, 2L),
                                body.getValue("selectedMembershipIds").jsonArray.map {
                                    it.jsonPrimitive.long
                                },
                            )
                            respond(
                                """{"photoId":7,"submittedAt":"2026-10-30T05:00:00Z","status":"PENDING"}""",
                                HttpStatusCode.Created,
                                headersOf(HttpHeaders.ContentType, "application/json"),
                            )
                        }
                    },
                )
            val upload =
                HttpClient(
                    MockEngine { request ->
                        assertEquals(HttpMethod.Put, request.method)
                        assertNull(request.headers[HttpHeaders.Authorization])
                        assertEquals("signed", request.headers["x-amz-checksum-sha256"])
                        assertEquals("storage.test", request.url.host)
                        uploaded = true
                        respond("", HttpStatusCode.OK)
                    },
                )
            api.use { client ->
                upload.use { uploadClient ->
                    val repository =
                        TeamAttendanceRepositoryImpl(
                            TeamPhotoRemoteDataSource(client, uploadClient),
                        )
                    val key = repository.uploadPhoto(5, "photo.png", "image/png", bytes)
                    assertEquals("team/key", key)
                    assertEquals(7L, repository.submitPhoto(5, key, setOf(1, 2)).photoId)
                    assertTrue(runCatching { repository.submitPhoto(5, key, emptySet()) }.isFailure)
                }
            }
        }

    @Test fun failedUploadDoesNotReturnReadyImageKey() =
        runTest {
            apiTestClient(
                MockEngine {
                    respond(
                        """
{"imageKey":"team/key","uploadUrl":"https://storage.test/photo",
"signedHeaders":{},
"expiresAt":"2099-01-01T00:00:00Z"}
""",
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                },
            ).use { api ->
                HttpClient(MockEngine { respond("", HttpStatusCode.Forbidden) }).use { upload ->
                    val repository =
                        TeamAttendanceRepositoryImpl(TeamPhotoRemoteDataSource(api, upload))
                    assertTrue(
                        runCatching {
                            repository.uploadPhoto(1, "test.png", "image/png", byteArrayOf(1))
                        }.isFailure,
                    )
                }
            }
        }
}
