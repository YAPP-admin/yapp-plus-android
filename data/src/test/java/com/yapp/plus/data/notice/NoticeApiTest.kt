package com.yapp.plus.data.notice

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.notice.NoticeType
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoticeApiTest {
    @Test fun filterCursorDatesAndDeadlineAreMapped() =
        runTest {
            apiTestClient(
                MockEngine { request ->
                    assertEquals("/api/posts", request.url.encodedPath)
                    assertEquals("ASSIGNMENT", request.url.parameters["type"])
                    assertEquals("opaque+/=", request.url.parameters["cursor"])
                    respond(
                        """
{"totalPostCount":31,
"items":[{"postId":8,"type":"ASSIGNMENT","title":"과제",
"createdDate":"2026-10-30",
"daysUntilDeadline":-1}],
"nextCursor":"next"}
""",
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                },
            ).use { client ->
                val page =
                    NoticeRepositoryImpl(
                        NoticeRemoteDataSource(client),
                    ).fetchNotices(NoticeType.ASSIGNMENT, "opaque+/=")
                assertEquals(31L, page.totalCount)
                assertEquals(-1L, page.items.single().daysUntilDeadline)
                assertEquals("next", page.nextCursor)
            }
        }

    @Test fun detailCarriesSubmissionImagesAndReadStateAndReadUsesPut() =
        runTest {
            val paths = mutableListOf<String>()
            apiTestClient(
                MockEngine { request ->
                    paths += request.url.encodedPath
                    if (request.method == HttpMethod.Put) {
                        respond("", HttpStatusCode.NoContent)
                    } else {
                        respond(
                            detailBody,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                },
            ).use { client ->
                val repository = NoticeRepositoryImpl(NoticeRemoteDataSource(client))
                val detail = repository.fetchNotice(8)
                assertEquals("https://example.com/submit", detail.assignment!!.submissionUrl)
                assertEquals(listOf("first", "second"), detail.images.map { it.key })
                assertFalse(detail.isRead)
                assertEquals(13L, detail.readCount)
                repository.markRead(8)
                assertEquals(listOf("/api/posts/8", "/api/posts/8/read"), paths)
            }
        }

    @Test fun malformedAndFailedResponsesAreNotReportedAsSuccess() =
        runTest {
            for ((body, status) in listOf(
                detailBody.replace("\"postId\":8", "\"postId\":9") to HttpStatusCode.OK,
                detailBody.replace("https://example.com/submit", "javascript:alert(1)") to
                    HttpStatusCode.OK,
                """{"code":"POST_NOT_FOUND"}""" to HttpStatusCode.NotFound,
            )) {
                apiTestClient(
                    MockEngine {
                        respond(
                            body,
                            status,
                            headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    },
                ).use { client ->
                    assertTrue(
                        runCatching {
                            NoticeRepositoryImpl(NoticeRemoteDataSource(client)).fetchNotice(8)
                        }.isFailure,
                    )
                }
            }
        }

    private val detailBody =
        """
{"postId":8,"title":"과제","type":"ASSIGNMENT",
"createdDate":"2026-10-30",
        "contentMarkdown":"**본문**","readCount":13,"read":false,
        "assignment":{"deadlineDate":"2026-11-01","positions":["PM","DESIGN"],
"submissionUrl":"https://example.com/submit",
"submissionLinkText":"제출"},
        "images":[
        {"imageKey":"second","filename":"2.png",
"displayOrder":1,
"readUrl":"https://example.com/2.png",
"readUrlExpiresAt":"2026-10-30T05:00:00Z"},
        {"imageKey":"first","filename":"1.png",
"displayOrder":0,
"readUrl":"https://example.com/1.png",
"readUrlExpiresAt":"2026-10-30T05:00:00Z"}]}
        """.trimIndent()
}
