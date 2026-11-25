package com.yapp.plus.data.notification

import com.yapp.plus.data.apiTestClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class NotificationApiTest {
    @Test
    fun mapsGeneralPostAndSessionRemindersAndPassesOpaqueCursor() =
        runTest {
            val requestedCursors = mutableListOf<String?>()
            val cursor = "next+page/="
            apiTestClient(
                MockEngine { request ->
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("/api/users/me/notifications", request.url.encodedPath)
                    assertEquals("Bearer test-access", request.headers[HttpHeaders.Authorization])
                    val requestedCursor = request.url.parameters["cursor"]
                    requestedCursors.add(requestedCursor)
                    val body =
                        if (requestedCursor == null) {
                            """
                            {
                              "items": [
                                {"id":9,"content":"일반 알림","sentDate":"2026-11-02",
                                 "type":"GENERAL","postId":null},
                                {"id":8,"content":"새 공지","sentDate":"2026-11-01",
                                 "type":"POST","postId":42},
                                {"id":7,"content":"세션 안내","sentDate":"2026-11-01",
                                 "type":"SESSION_REMINDER","postId":null,"sessionId":71,
                                 "navigationTarget":"SESSION_SCHEDULE"},
                                {"id":6,"content":"세션 안내","sentDate":"2026-11-01",
                                 "type":"SESSION_REMINDER","postId":null,"sessionId":0,
                                 "navigationTarget":"SESSION_SCHEDULE"}
                              ],
                              "nextCursor":"$cursor"
                            }
                            """.trimIndent()
                        } else {
                            """{"items":[],"nextCursor":null}"""
                        }
                    respond(body, headers = headersOf(HttpHeaders.ContentType, "application/json"))
                },
            ).use { client ->
                val repository = NotificationRepositoryImpl(NotificationRemoteDataSource(client))
                val page = repository.fetchNotifications()
                assertEquals(listOf(9L, 8L, 7L, 6L), page.items.map { it.id })
                assertEquals(LocalDate.of(2026, 11, 2), page.items.first().sentDate)
                assertEquals(listOf(null, 42L, null, null), page.items.map { it.postId })
                assertEquals(listOf(null, null, 71L, null), page.items.map { it.sessionId })
                assertEquals(cursor, page.nextCursor)
                assertNull(repository.fetchNotifications(page.nextCursor).nextCursor)
                assertEquals(listOf(null, cursor), requestedCursors)
            }
        }
}
