package com.yapp.plus.data.afterparty

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.result.YappResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
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
}

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
