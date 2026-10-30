package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.auth.datasource.auth.AuthRemoteDataSource
import com.yapp.plus.data.auth.datasource.auth.AuthResponseResult
import com.yapp.plus.data.auth.repository.AuthRepositoryImpl
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.RegistrationFieldError
import com.yapp.plus.domain.auth.RegistrationFieldError.Reason
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
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
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRemoteDataSourceTest {
    @Test
    fun `google authentication sends token and nonce and handles signed in result`() =
        runTest {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals(
                        "/api/auth/oidc/google/native-authentications",
                        request.url.encodedPath,
                    )
                    val content = (request.body as TextContent).text
                    val body = Json.parseToJsonElement(content).jsonObject
                    assertEquals("test-id-token", body.getValue("idToken").jsonPrimitive.content)
                    assertEquals("test-nonce", body.getValue("nonce").jsonPrimitive.content)
                    respond(
                        """
{"result":"SIGNED_IN",
"user":{"id":1,"name":"김야뿌"},
"session":{"accessToken":"acc","refreshToken":"ref"}}
""",
                        HttpStatusCode.OK,
                        jsonHeaders,
                    )
                }

            createClient(engine).use { client ->
                val result =
                    AuthRemoteDataSource(
                        client,
                    ).authenticateWithGoogle("test-id-token", "test-nonce")
                assertTrue(result is AuthResponseResult.SignedIn)
                val signedIn = result as AuthResponseResult.SignedIn
                assertEquals("acc", signedIn.accessToken)
                assertEquals("ref", signedIn.refreshToken)
                assertEquals("김야뿌", signedIn.userName)
            }
        }

    @Test
    fun `google authentication handles signup required result`() =
        runTest {
            val engine =
                MockEngine {
                    respond(
                        """{"result":"SIGNUP_REQUIRED","registrationToken":"reg-token"}""",
                        HttpStatusCode.OK,
                        jsonHeaders,
                    )
                }

            createClient(engine).use { client ->
                val result =
                    AuthRemoteDataSource(client).authenticateWithGoogle("id-token", "nonce")
                assertTrue(result is AuthResponseResult.SignupRequired)
                val signupRequired = result as AuthResponseResult.SignupRequired
                assertEquals("reg-token", signupRequired.registrationToken)
            }
        }

    @Test
    fun `user registration sends registration token name and phone number`() =
        runTest {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/api/users", request.url.encodedPath)
                    val content = (request.body as TextContent).text
                    val body = Json.parseToJsonElement(content).jsonObject
                    assertEquals(
                        "reg-token",
                        body.getValue("registrationToken").jsonPrimitive.content,
                    )
                    assertEquals("홍길동", body.getValue("name").jsonPrimitive.content)
                    assertEquals("01012345678", body.getValue("phoneNumber").jsonPrimitive.content)
                    respond(
                        """{"result":"SIGNED_IN","session":{"accessToken":"acc","refreshToken":"ref"}}""",
                        HttpStatusCode.OK,
                        jsonHeaders,
                    )
                }

            createClient(engine).use { client ->
                val result =
                    AuthRemoteDataSource(client).register("reg-token", "홍길동", "01012345678")
                assertTrue(result is AuthResponseResult.SignedIn)
                val signedIn = result as AuthResponseResult.SignedIn
                assertEquals("acc", signedIn.accessToken)
                assertEquals("ref", signedIn.refreshToken)
            }
        }

    @Test
    fun `invalid session response returns unavailable failure`() =
        runTest {
            val responses =
                listOf(
                    """{"result":"SIGNED_IN","session":{"accessToken":"","refreshToken":"ref"}}""",
                    """{"result":"SIGNED_IN","session":{"accessToken":"acc","refreshToken":""}}""",
                    """{"result":"SIGNUP_REQUIRED","registrationToken":""}""",
                    """{"result":"UNKNOWN_RESULT"}""",
                )

            for (response in responses) {
                val engine = MockEngine { respond(response, HttpStatusCode.OK, jsonHeaders) }
                createClient(engine).use { client ->
                    val result = AuthRemoteDataSource(client).authenticateWithGoogle("id", "nonce")
                    assertEquals(AuthResponseResult.Failed(AuthFailure.Unavailable), result)
                }
            }
        }

    @Test
    fun `error response code maps to expected auth failure`() =
        runTest {
            val testCases =
                listOf(
                    "INVALID_REQUEST" to AuthFailure.InvalidRequest,
                    "AUTHENTICATION_FAILED" to AuthFailure.AuthenticationRejected,
                    "AUTHENTICATION_STATE_INVALID" to AuthFailure.AuthenticationRejected,
                    "USER_ALREADY_EXISTS" to AuthFailure.UserAlreadyExists,
                    "USER_REGISTRATION_FAILED" to AuthFailure.Unavailable,
                    "SOME_OTHER_ERROR" to AuthFailure.Unavailable,
                )

            for ((code, expectedFailure) in testCases) {
                val engine =
                    MockEngine {
                        respond("""{"code":"$code"}""", HttpStatusCode.BadRequest, jsonHeaders)
                    }
                createClient(engine).use { client ->
                    val result = AuthRemoteDataSource(client).authenticateWithGoogle("id", "nonce")
                    assertEquals(AuthResponseResult.Failed(expectedFailure), result)
                }
            }
        }

    @Test
    fun `registration field errors reach the repository with typed fields and reasons`() =
        runTest {
            val testCases =
                listOf(
                    Triple("name", "REQUIRED", RegistrationFieldError.Name(Reason.Required)),
                    Triple(
                        "name",
                        "INVALID_FORMAT",
                        RegistrationFieldError.Name(Reason.InvalidFormat),
                    ),
                    Triple("name", "TOO_LONG", RegistrationFieldError.Name(Reason.TooLong)),
                    Triple(
                        "phoneNumber",
                        "REQUIRED",
                        RegistrationFieldError.PhoneNumber(Reason.Required),
                    ),
                    Triple(
                        "phoneNumber",
                        "INVALID_FORMAT",
                        RegistrationFieldError.PhoneNumber(Reason.InvalidFormat),
                    ),
                    Triple(
                        "phoneNumber",
                        "ALREADY_EXISTS",
                        RegistrationFieldError.PhoneNumber(Reason.AlreadyExists),
                    ),
                    Triple(
                        "phoneNumber",
                        "FUTURE_CODE",
                        RegistrationFieldError.PhoneNumber(Reason.Unknown),
                    ),
                )
            for ((field, code, expectedError) in testCases) {
                val isConflict = code == "ALREADY_EXISTS"
                val responseCode = if (isConflict) "USER_ALREADY_EXISTS" else "INVALID_REQUEST"
                val failure =
                    if (isConflict) AuthFailure.UserAlreadyExists else AuthFailure.InvalidRequest
                val engine =
                    MockEngine {
                        respond(
                            """
                            {"code":"$responseCode","fieldErrors":[
                              {"field":"$field","code":"$code","message":"표시 문구"}
                            ]}
                            """.trimIndent(),
                            if (isConflict) HttpStatusCode.Conflict else HttpStatusCode.BadRequest,
                            jsonHeaders,
                        )
                    }
                createClient(engine).use { client ->
                    val tokenStore = TokenStore()
                    val repository =
                        AuthRepositoryImpl(
                            AuthRemoteDataSource(client),
                            tokenStore,
                            FakeSessionProfileDataSource(),
                        )
                    val result =
                        repository.register("reg-token", Name("이름"), PhoneNumber("01012345678"))
                    assertEquals(AuthResult.Failed(failure, listOf(expectedError)), result)
                    assertEquals(null, tokenStore.currentTokens())
                }
            }
        }

    @Test
    fun `registration keeps both known field errors and ignores unknown fields`() =
        runTest {
            val engine =
                MockEngine {
                    respond(
                        """
                        {"code":"INVALID_REQUEST","fieldErrors":[
                          {"field":"name","code":"TOO_LONG"},
                          {"field":"phoneNumber","code":"INVALID_FORMAT"},
                          {"field":"futureField","code":"REQUIRED"}
                        ]}
                        """.trimIndent(),
                        HttpStatusCode.BadRequest,
                        jsonHeaders,
                    )
                }
            createClient(engine).use { client ->
                val result =
                    AuthRemoteDataSource(client).register("reg-token", "이름", "+821012345678")
                assertEquals(
                    AuthResponseResult.Failed(
                        AuthFailure.InvalidRequest,
                        listOf(
                            RegistrationFieldError.Name(Reason.TooLong),
                            RegistrationFieldError.PhoneNumber(Reason.InvalidFormat),
                        ),
                    ),
                    result,
                )
            }
        }

    private fun createClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(DefaultRequest) { url("https://api.example.test/api/") }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private val jsonHeaders =
        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}
