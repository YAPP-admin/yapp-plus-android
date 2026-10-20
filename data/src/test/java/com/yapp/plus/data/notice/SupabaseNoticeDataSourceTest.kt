package com.yapp.plus.data.notice

import com.yapp.plus.data.supabase.SupabaseDataSourceException
import com.yapp.plus.data.supabase.SupabaseTestFixture
import com.yapp.plus.domain.notice.NoticeType
import io.ktor.client.engine.mock.respond
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class SupabaseNoticeDataSourceTest {
    @Test
    fun `list applies optional type and stable newest order and maps plain text`() =
        runTest {
            for (type in listOf(null, NoticeType.OPERATION, NoticeType.SESSION)) {
                SupabaseTestFixture(handler = { request ->
                    assertEquals("/rest/v1/mvp_notices", request.url.encodedPath)
                    assertEquals("published_at.desc,id.desc", request.url.parameters["order"])
                    val expectedType = type?.let { "eq.${it.name}" }
                    assertEquals(expectedType, request.url.parameters["notice_type"])
                    respond("[$notice]")
                }).use {
                    val repository = NoticeRepositoryImpl(SupabaseNoticeDataSource(it.client))
                    val result = repository.fetchNotices(type).single()
                    assertEquals(7L, result.id)
                    assertEquals("[더미] 공지", result.title)
                    assertEquals("<plain>본문</plain>", result.content)
                    assertEquals(NoticeType.SESSION, result.type)
                    assertEquals(Instant.parse("2026-10-20T01:02:03Z"), result.publishedAt)
                }
            }
        }

    @Test
    fun `detail filters id and maps missing detail to null`() =
        runTest {
            for (body in listOf("[$notice]", "[]")) {
                SupabaseTestFixture(handler = { request ->
                    assertEquals("eq.7", request.url.parameters["id"])
                    assertEquals("1", request.url.parameters["limit"])
                    respond(body)
                }).use {
                    val repository = NoticeRepositoryImpl(SupabaseNoticeDataSource(it.client))
                    val result = repository.fetchNotice(7)
                    if (body == "[]") assertNull(result) else assertEquals(7L, result?.id)
                }
            }
            SupabaseTestFixture(handler = { respond("[]") }).use {
                val repository = NoticeRepositoryImpl(SupabaseNoticeDataSource(it.client))
                assertTrue(repository.fetchNotices().isEmpty())
            }
        }

    @Test
    fun `unknown type invalid timestamp mismatched id and malformed responses fail safely`() =
        runTest {
            val responses =
                listOf(
                    "{}",
                    "[$notice,$notice]",
                    "[${notice.replace("SESSION", "UNKNOWN")}]",
                    "[${notice.replace("2026-10-20T01:02:03Z", "invalid")}]",
                    "[${notice.replace("7", "8")}]",
                )
            for (body in responses) {
                SupabaseTestFixture(handler = { respond(body) }).use {
                    val failure =
                        runCatching {
                            SupabaseNoticeDataSource(it.client).fetchNotice(7)
                        }.exceptionOrNull()
                    assertTrue(failure is SupabaseDataSourceException)
                    assertNull(failure?.cause)
                }
            }
        }

    private val notice =
        """{"id":7,"title":"[더미] 공지","content":"<plain>본문</plain>",
        "notice_type":"SESSION","published_at":"2026-10-20T01:02:03Z"}"""
}
