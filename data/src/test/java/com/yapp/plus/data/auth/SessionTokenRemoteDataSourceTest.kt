package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.RefreshResult
import com.yapp.plus.data.auth.datasource.session.SessionTokenRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionTokenRemoteDataSourceTest {
    @Test
    fun `renewal sends refresh token without authorization and reads rotated pair`() =
        runTest {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/api/auth/session-renewals", request.url.encodedPath)
                    assertNull(request.headers[HttpHeaders.Authorization])
                    val content = (request.body as TextContent).text
                    val body = Json.parseToJsonElement(content).jsonObject
                    assertEquals("old-refresh", body.getValue("refreshToken").jsonPrimitive.content)
                    respond(
                        """{"result":"SIGNED_IN","user":{},"session":{
                    "accessToken":"new-access","refreshToken":"new-refresh","expiresIn":3600}}""",
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            createClient(engine).use { client ->
                val result = SessionTokenRemoteDataSource(client).refresh("old-refresh")
                assertTrue(result is RefreshResult.Success)
                val tokens = (result as RefreshResult.Success).tokens
                assertEquals("new-access", tokens.accessToken)
                assertEquals("new-refresh", tokens.refreshToken)
            }
        }

    @Test
    fun `unauthorized renewal returns rejected result`() =
        runTest {
            createClient(MockEngine { respond("", HttpStatusCode.Unauthorized) }).use { client ->
                assertEquals(
                    RefreshResult.Rejected,
                    SessionTokenRemoteDataSource(client).refresh("invalid-refresh"),
                )
            }
        }

    @Test
    fun `server error propagates without exposing response body`() =
        runTest {
            val engine =
                MockEngine { respond("sensitive body", HttpStatusCode.InternalServerError) }
            createClient(engine).use { client ->
                val failure =
                    runCatching { SessionTokenRemoteDataSource(client).refresh("refresh") }
                        .exceptionOrNull()
                assertTrue(failure is ResponseException)
                assertTrue(failure?.message?.contains("sensitive body") == false)
            }
        }

    @Test
    fun `successful response without rotated refresh token is rejected`() =
        runTest {
            val engine =
                MockEngine {
                    respond(
                        """{"result":"SIGNED_IN","session":{"accessToken":"new-access"}}""",
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            createClient(engine).use { client ->
                assertEquals(
                    RefreshResult.Rejected,
                    SessionTokenRemoteDataSource(client).refresh("refresh"),
                )
            }
        }

    private fun createClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(DefaultRequest) { url("https://api.example.test/api/") }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
}
