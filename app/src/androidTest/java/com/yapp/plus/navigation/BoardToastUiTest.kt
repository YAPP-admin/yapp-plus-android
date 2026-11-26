package com.yapp.plus.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.components.toast.YappToastManager
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.di.AuthE2ETestEntryPoint
import com.yapp.plus.feature.main.board.BoardRoute
import com.yapp.plus.testing.NavigationTestActivity
import dagger.hilt.android.EntryPointAccessors
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class BoardToastUiTest {
    @get:Rule val compose = createAndroidComposeRule<NavigationTestActivity>()

    @Test
    fun readFailureSurvivesRouteAbsenceAndIsNotReplayedAfterConsumption() {
        val readResponse = CompletableDeferred<Unit>()
        val readRequests = AtomicInteger()
        val engine =
            MockEngine { request ->
                val isRead = request.url.encodedPath.endsWith("/read")
                if (isRead) {
                    readRequests.incrementAndGet()
                    readResponse.await()
                }
                val body =
                    if (request.url.encodedPath.endsWith("/posts/1")) {
                        """
                        {"postId":1,"title":"이벤트 테스트 공지","contentMarkdown":"공지 본문",
                         "type":"SESSION","createdDate":"2026-11-26","assignment":null,
                         "images":[],"readCount":0,"read":false}
                        """.trimIndent()
                    } else {
                        """{"totalPostCount":0,"items":[],"nextCursor":null}"""
                    }
                respond(
                    body,
                    status = if (isRead) HttpStatusCode.InternalServerError else HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }.use { client ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val entryPoint =
                EntryPointAccessors.fromApplication(context, AuthE2ETestEntryPoint::class.java)
            val active = AtomicReference<HttpClient?>(client)
            entryPoint.authenticatedClient().plugin(HttpSend).intercept { request ->
                val fake = active.get() ?: return@intercept execute(request)
                fake.request(request).call
            }
            try {
                verifyReadFailure(readResponse, readRequests)
            } finally {
                active.set(null)
            }
        }
    }

    private fun verifyReadFailure(
        response: CompletableDeferred<Unit>,
        requests: AtomicInteger,
    ) {
        val visible = mutableStateOf(true)
        val toastManager = YappToastManager()
        val error = "읽음 확인을 완료하지 못했어요. 다시 시도해 주세요."
        compose.setContent {
            YappTheme {
                if (visible.value) {
                    BoardRoute(
                        toastManager = toastManager,
                        noticeId = 1,
                        onNoticeClick = {},
                        onBack = {},
                    )
                }
            }
        }
        waitForText("확인하기")
        compose.onNodeWithText("확인하기").performScrollTo().performClick()
        compose.waitUntil(10_000) { requests.get() == 1 }
        compose.runOnIdle { visible.value = false }
        response.complete(Unit)
        compose.waitForIdle()
        compose.runOnIdle { visible.value = true }
        waitForText(error)
        compose.onNodeWithText(error).assertIsDisplayed()

        compose.runOnIdle { visible.value = false }
        compose.waitForIdle()
        compose.runOnIdle { visible.value = true }
        compose.waitForIdle()
        compose.onNodeWithText(error).assertDoesNotExist()
        compose.onNodeWithText("확인하기").performScrollTo().performClick()
        waitForText(error)
        compose.onNodeWithText(error).assertIsDisplayed()
        assertEquals(2, requests.get())
    }

    private fun waitForText(text: String) {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
