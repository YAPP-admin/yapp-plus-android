package com.yapp.plus.data.notification

import com.yapp.plus.data.apiTestClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationDeviceApiTest {
    @Test fun registrationAndRemovalUseSessionApiAndExplicitAndroidPlatform() =
        runTest {
            val methods = mutableListOf<HttpMethod>()
            apiTestClient(
                MockEngine { request ->
                    methods += request.method
                    assertEquals(
                        "/api/users/me/notification-devices/test-installation_1",
                        request.url.encodedPath,
                    )
                    if (request.method == HttpMethod.Put) {
                        val body =
                            Json
                                .parseToJsonElement(
                                    (request.body as TextContent).text,
                                ).jsonObject
                        assertEquals("ANDROID", body.getValue("platform").jsonPrimitive.content)
                        assertEquals(
                            "test-fcm-token",
                            body.getValue("registrationToken").jsonPrimitive.content,
                        )
                    }
                    respond("", HttpStatusCode.NoContent)
                },
            ).use { client ->
                val repository =
                    NotificationDeviceRepositoryImpl(NotificationDeviceRemoteDataSource(client))
                repository.register("test-installation_1", "test-fcm-token")
                repository.unregister("test-installation_1")
                assertEquals(listOf(HttpMethod.Put, HttpMethod.Delete), methods)
                assertTrue(runCatching { repository.register("../invalid", "token") }.isFailure)
                assertTrue(
                    runCatching { repository.register("installation", "bad token") }.isFailure,
                )
                assertEquals(2, methods.size)
            }
        }
}
