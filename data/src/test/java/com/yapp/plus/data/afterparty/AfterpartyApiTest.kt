package com.yapp.plus.data.afterparty

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.SettlementAccount
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.result.YappResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AfterpartyApiTest {
    @Test
    fun keepsFilterAndOpaqueCursorAndMapsVotingConflict() =
        runTest {
            apiTestClient(
                MockEngine { request ->
                    assertEquals("Bearer test-access", request.headers[HttpHeaders.Authorization])
                    when (request.url.encodedPath) {
                        "/api/afterparties" -> {
                            assertEquals("CLOSED", request.url.parameters["filter"])
                            assertEquals("opaque+/=", request.url.parameters["cursor"])
                            respond("""{"items":[],"nextCursor":null}""", headers = jsonHeaders)
                        }
                        "/api/afterparties/9/response" -> {
                            assertEquals(HttpMethod.Put, request.method)
                            assertEquals("""{"response":"ATTENDING"}""", (request.body as TextContent).text)
                            respond(
                                """{"code":"AFTERPARTY_VOTING_CLOSED"}""",
                                status = HttpStatusCode.Conflict,
                                headers = jsonHeaders,
                            )
                        }
                        else -> error(request.url.encodedPath)
                    }
                },
            ).use { client ->
                val repository = AfterpartyRepositoryImpl.create(client)
                val page = repository.fetchAfterparties(AfterpartyFilter.CLOSED, "opaque+/=")
                assertNull((page as YappResult.Success).value.nextCursor)
                assertEquals(
                    YappResult.Failure(AfterpartyFailure.VotingClosed),
                    repository.updateResponse(9, AfterpartyResponse.ATTENDING),
                )
            }
        }

    @Test
    fun sendsRevisionAndKeepsAccountAndAttachmentOrder() =
        runTest {
            apiTestClient(
                MockEngine { request ->
                    assertEquals(HttpMethod.Put, request.method)
                    assertEquals("/api/afterparties/9/settlements/13", request.url.encodedPath)
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals("0001-234", body.getValue("accountNumber").jsonPrimitive.content)
                    assertEquals(
                        listOf("41", "22"),
                        body.getValue("imageIds").jsonArray.map { it.jsonPrimitive.content },
                    )
                    assertEquals("4", body.getValue("expectedRevision").jsonPrimitive.content)
                    respond(
                        """{"code":"SETTLEMENT_REVISION_CONFLICT"}""",
                        status = HttpStatusCode.Conflict,
                        headers = jsonHeaders,
                    )
                },
            ).use { client ->
                val draft =
                    SettlementDraft(
                        SettlementAccount(SettlementBank.TOSS, "0001-234", "정산자"),
                        10_000,
                        4_000,
                        setOf(31),
                        listOf(41, 22),
                    )
                val repository = SettlementRepositoryImpl.create(client, client)
                assertEquals(
                    YappResult.Failure(AfterpartyFailure.RevisionConflict),
                    repository.updateSettlement(9, 13, 4, draft),
                )
            }
        }

    @Test
    fun sendsSignedUploadWithSeparateClient() =
        runTest {
            val bytes = byteArrayOf(1, 2, 3)
            apiTestClient(
                MockEngine { request ->
                    assertEquals("/api/afterparties/9/settlement-images/presign", request.url.encodedPath)
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals("13", body.getValue("settlementId").jsonPrimitive.content)
                    assertEquals(
                        "A5BYxvLAy0ksUzsKTRTvd8wPeKvMztUofYShogEc+4E=",
                        body.getValue("checksumSha256").jsonPrimitive.content,
                    )
                    respond(
                        """
                        {"imageId":41,"uploadUrl":"https://upload.example.invalid/receipt",
                         "signedHeaders":{"x-signed":"value"},"expiresAt":"2099-01-01T00:00:00Z"}
                        """.trimIndent(),
                        headers = jsonHeaders,
                    )
                },
            ).use { client ->
                HttpClient(
                    MockEngine { request ->
                        assertEquals(HttpMethod.Put, request.method)
                        assertEquals("upload.example.invalid", request.url.host)
                        assertNull(request.headers[HttpHeaders.Authorization])
                        assertEquals("value", request.headers["x-signed"])
                        assertArrayEquals(bytes, (request.body as OutgoingContent.ByteArrayContent).bytes())
                        respond("", status = HttpStatusCode.OK)
                    },
                ).use { upload ->
                    val repository = SettlementRepositoryImpl.create(client, upload)
                    assertEquals(
                        YappResult.Success(41L),
                        repository.uploadPhoto(9, 13, "receipt.png", "image/png", bytes),
                    )
                }
            }
        }
}

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
