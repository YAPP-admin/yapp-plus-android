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
    fun mapsGeneralAndPostAndPassesOpaqueCursor() =
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
                                 "type":"POST","postId":42}
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
                assertEquals(listOf(9L, 8L), page.items.map { it.id })
                assertEquals(LocalDate.of(2026, 11, 2), page.items.first().sentDate)
                assertEquals(listOf(null, 42L), page.items.map { it.postId })
                assertEquals(cursor, page.nextCursor)
                assertNull(repository.fetchNotifications(page.nextCursor).nextCursor)
                assertEquals(listOf(null, cursor), requestedCursors)
            }
        }
}
