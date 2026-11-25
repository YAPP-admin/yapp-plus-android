package com.yapp.plus.navigation

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.di.AuthE2ETestEntryPoint
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
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalTestApi::class)
class AfterpartyUiTest {
    @get:Rule val compose = createAndroidComposeRule<NavigationTestActivity>()

    @Test
    fun communityVoteOpensAttendeesAndCanMarkPersonalPayment() {
        val fixtures = AfterpartyUiFixtures()
        openCommunity(fixtures) {
            waitFor("테스트 뒤풀이")
            compose.onNodeWithText("테스트 뒤풀이").performClick()
            waitFor("제출하기")
            assertEquals(0, fixtures.attendeeReads.get())
            compose.onNodeWithText("참석").performClick()
            compose.onNodeWithText("제출하기").performClick()
            waitFor("테스트 참석자")
            compose.onNodeWithText("테스트 참석자").assertIsDisplayed()
            assertEquals(1, fixtures.votes.get())
            waitFor("테스트 정산자님 정산")
            compose.onNodeWithText("테스트 정산자님 정산").performScrollTo().performClick()
            waitFor("송금했어요")
            compose.onNodeWithText("송금했어요").performScrollTo().performClick()
            waitFor("되돌리기")
            assertEquals(1, fixtures.payments.get())
        }
    }

    @Test
    fun communityShowsOneIndicatorDuringInitialLoadAndPullToRefresh() {
        val initialResponse = CompletableDeferred<Unit>()
        val refreshResponse = CompletableDeferred<Unit>()
        val requests = AtomicInteger()
        val fixtures =
            AfterpartyUiFixtures {
                when (requests.incrementAndGet()) {
                    1 -> initialResponse.await()
                    2 -> refreshResponse.await()
                    else -> Unit
                }
            }
        val progress =
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo.Indeterminate,
            )
        openCommunity(fixtures) {
            compose.waitUntil(10_000) { requests.get() == 1 }
            compose.onAllNodes(progress).assertCountEquals(1)
            initialResponse.complete(Unit)
            waitFor("테스트 뒤풀이")
            compose.onAllNodes(progress).assertCountEquals(0)

            compose.onNode(hasScrollAction()).performTouchInput { swipeDown() }
            compose.waitUntil(10_000) { requests.get() == 2 }
            compose.onNodeWithText("테스트 뒤풀이").assertIsDisplayed()
            compose.onAllNodes(progress).assertCountEquals(1)
            refreshResponse.complete(Unit)
            compose.waitUntil(10_000) {
                compose.onAllNodes(progress).fetchSemanticsNodes(atLeastOneRootRequired = false).isEmpty()
            }
        }
    }

    @Test
    fun settlementNotificationOpensDetail() {
        openCommunity(AfterpartyUiFixtures(), startInCommunity = false) {
            openSettlementNotification()
            waitFor("테스트 정산자님 정산")
        }
    }

    private fun openSettlementNotification() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithContentDescription("알림").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("알림").performClick()
        waitFor("뒤풀이 정산이 등록됐어요")
        compose.onNodeWithText("뒤풀이 정산이 등록됐어요").assertIsDisplayed().performClick()
    }

    private fun openCommunity(
        fixtures: AfterpartyUiFixtures,
        startInCommunity: Boolean = true,
        assertions: () -> Unit,
    ) {
        fixtures.client().use { fake ->
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val api = EntryPointAccessors.fromApplication(context, AuthE2ETestEntryPoint::class.java)
            val active = AtomicReference<HttpClient?>(fake)
            api.authenticatedClient().plugin(HttpSend).intercept { request ->
                val client = active.get() ?: return@intercept execute(request)
                client.request(request).call
            }
            try {
                compose.setContent {
                    YappTheme { AppNavHost(onExit = {}, startDestination = AppNavigationDestination.Main) }
                }
                if (startInCommunity) compose.onNodeWithText("커뮤니티").performClick()
                assertions()
            } finally {
                active.set(null)
            }
        }
    }

    private fun waitFor(value: String) {
        compose.waitUntil(20_000) {
            compose.onAllNodesWithText(value).fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty()
        }
    }
}

internal class AfterpartyUiFixtures(
    private val awaitAfterparties: suspend () -> Unit = {},
) {
    val votes = AtomicInteger()
    val attendeeReads = AtomicInteger()
    val payments = AtomicInteger()
    private val paid = AtomicBoolean()

    fun client(): HttpClient =
        HttpClient(
            MockEngine { request ->
                val path = request.url.encodedPath
                val body =
                    when {
                        path.endsWith("/current-week") ->
                            """{"weekStartDate":"2026-11-22","weekEndDate":"2026-11-28","session":null}"""
                        path.endsWith("/posts") ->
                            """{"totalPostCount":0,"items":[],"nextCursor":null}"""
                        path.endsWith("/notifications") ->
                            """
                            {"items":[{"id":1,"content":"뒤풀이 정산이 등록됐어요","sentDate":"2026-11-25",
                             "type":"SETTLEMENT","postId":null,"navigationTarget":"SETTLEMENT",
                             "afterpartyId":9,"settlementId":13}],"nextCursor":null}
                            """.trimIndent()
                        path.endsWith("/afterparties") -> {
                            if (request.url.parameters["filter"] == "ALL") awaitAfterparties()
                            """{"items":[$listItem],"nextCursor":null}"""
                        }
                        path.endsWith("/afterparties/9/response") -> {
                            assertEquals(HttpMethod.Put, request.method)
                            assertEquals("""{"response":"ATTENDING"}""", (request.body as TextContent).text)
                            votes.incrementAndGet()
                            """{"response":"ATTENDING"}"""
                        }
                        path.endsWith("/afterparties/9/attendees") -> attendees()
                        path.endsWith("/afterparties/9/settlements") -> {
                            """{"items":[${settlementItem()}],"nextCursor":null}"""
                        }
                        path.endsWith("/afterparties/9/settlements/13/payment") -> {
                            assertEquals(HttpMethod.Put, request.method)
                            assertEquals("""{"paymentStatus":"PAID"}""", (request.body as TextContent).text)
                            payments.incrementAndGet()
                            paid.set(true)
                            """{"paymentStatus":"PAID"}"""
                        }
                        path.endsWith("/afterparties/9/settlements/13") -> settlementDetail()
                        path.endsWith("/afterparties/9") -> afterpartyDetail()
                        else -> error("Unexpected mock path $path")
                    }
                respond(body, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            },
        ) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private fun afterpartyDetail(): String {
        val response = if (votes.get() == 0) "UNANSWERED" else "ATTENDING"
        val count = if (votes.get() == 0) "null" else "1"
        return """
            {"afterpartyId":9,"title":"테스트 뒤풀이","content":"세션 뒤풀이 안내",$schedule,
             "state":"OPEN","response":"$response","attendingCount":$count}
            """.trimIndent()
    }

    private fun attendees(): String {
        attendeeReads.incrementAndGet()
        return """
            {"items":[{"recipientId":31,"name":"테스트 참석자","teamName":null,
              "position":null,"isMe":false}],"nextCursor":null}
            """.trimIndent()
    }

    private fun settlementItem(): String {
        val status =
            when {
                paid.get() -> "\"PAID\""
                else -> "\"UNPAID\""
            }
        return """
            {"settlementId":13,"afterpartyId":9,"creatorName":"테스트 정산자","isMe":false,
             "participantCount":3,"myAmount":12000,"paymentStatus":$status}
            """.trimIndent()
    }

    private fun settlementDetail(): String =
        """
        ${settlementItem().dropLast(1)},"totalAmount":36000,"perPersonAmount":12000,
         "bank":"TOSS","accountNumber":"001-234","accountHolder":"테스트 정산자",
         "debtorCount":2,"paidCount":0,"recipientIds":[],"photos":[],"revision":0}
        """.trimIndent()

    private val schedule =
        """
        "startsAt":"2030-03-21T09:30:00Z","startsAtDisplay":"03.21 [토] 18:30",
        "deadline":"2030-03-20T14:59:00Z","deadlineDisplay":"03.20 [금] 23:59"
        """.trimIndent()

    private val listItem =
        """
        {"afterpartyId":9,"title":"테스트 뒤풀이",$schedule,"respondedCount":0,"state":"OPEN"}
        """.trimIndent()
}
