package com.yapp.plus.navigation

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalTestApi::class)
class AfterpartyUiTest {
    @get:Rule val compose = createAndroidComposeRule<NavigationTestActivity>()

    @Test
    fun settlementEditorKeepsDecimalPreviewOutOfTheSubmittedAmount() {
        val fixtures = AfterpartyUiFixtures(eventEnded = true)
        fixtures.votes.set(1)
        openCommunity(fixtures) {
            waitFor("테스트 뒤풀이")
            compose.onNodeWithText("테스트 뒤풀이").performClick()
            waitFor("정산 추가하기")
            compose.onNodeWithText("종료").assertIsDisplayed()
            compose.onNodeWithText("정산 추가하기").assertIsDisplayed().performClick()
            waitFor("정산할 사람 3명")
            compose.onNodeWithText("정산 요청하기 (2명)").assertIsNotEnabled()
            compose.onNodeWithText("정산할 사람 3명").performClick()
            compose.onNodeWithText("제외할 참석자").performClick()
            compose.onNodeWithText("다음").performClick()

            compose.onNodeWithContentDescription("총 금액").performTextInput("100001")
            compose.onNodeWithText("은행 선택").performScrollTo().performClick()
            compose.onNodeWithText("토스뱅크").performClick()
            compose.onNodeWithContentDescription("계좌번호").performTextInput("001-234")
            compose.onNodeWithContentDescription("예금주").performScrollTo().performTextInput("테스트 정산자")
            compose.onNodeWithText("소수점 포함 금액보기").performScrollTo().performClick()
            compose.onNodeWithText("50,000.5원").assertIsDisplayed()
            compose.onNodeWithText("정산 요청하기 (1명)").assertIsDisplayed().performClick()

            compose.waitUntil(10_000) { fixtures.createdSettlement.get() != null }
            val expected =
                """
                {"bank":"TOSS","accountNumber":"001-234","accountHolder":"테스트 정산자",
                 "totalAmount":100001,"perPersonAmount":50000,"recipientIds":[31],"imageIds":[]}
                """.trimIndent()
            assertEquals(Json.parseToJsonElement(expected), fixtures.createdSettlement.get())
            waitFor("1명에게 정산을 요청했어요")
            compose.onNodeWithText("확인").performClick()
            compose.onNodeWithText("1명에게 정산을 요청했어요").assertDoesNotExist()
        }
    }

    @Test
    fun communityListShowsFigmaTabsAndFiltersWithServerVoteCounts() {
        openCommunity(AfterpartyUiFixtures(includeEndedAfterparty = true)) {
            waitFor("테스트 뒤풀이")
            compose.onNodeWithText("뒤풀이").assertIsSelected()
            compose.onNodeWithText("전체").assertIsSelected()
            val openParty =
                hasText("테스트 뒤풀이") and
                    hasText("03.23 (토) 18:30") and
                    hasText("42명 투표 완료")
            compose.onNode(openParty).assertIsDisplayed()

            val isFilter = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
            val closedFilter = compose.onNode(isFilter and hasText("투표 마감"))
            closedFilter.performClick()
            waitFor("지난 뒤풀이")
            closedFilter.assertIsSelected()
            compose.onNodeWithText("테스트 뒤풀이").assertDoesNotExist()
            val endedParty =
                hasText("지난 뒤풀이") and
                    hasText("투표 마감") and
                    hasText("32명 투표 완료")
            compose.onNode(endedParty).assertIsDisplayed()

            compose.onNodeWithText("모임").performClick().assertIsSelected()
            compose.onNodeWithText("모임 목록을 준비 중이에요").assertIsDisplayed()
            compose.onNodeWithText("소모임").assertIsSelected()
            compose.onNodeWithText("지난 뒤풀이").assertDoesNotExist()
            compose.onNodeWithText("전체").performClick().assertIsSelected()
            compose.onNodeWithText("번개").performClick().assertIsSelected()

            compose.onNodeWithText("뒤풀이").performClick().assertIsSelected()
            closedFilter.assertIsSelected()
            compose.onNode(endedParty).assertIsDisplayed()

            compose.onNodeWithText("모임").performClick()
            compose.onNodeWithText("번개").assertIsSelected()
        }
    }

    @Test
    fun communityVoteOpensAttendeesAndCanMarkPersonalPayment() {
        val fixtures = AfterpartyUiFixtures(includeAnotherPayment = true)
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
            waitFor("0 / 2 완료")
            compose.onNodeWithText("20,000원").assertIsDisplayed()
            compose.onAllNodesWithText("송금했어요")[0].performScrollTo().performClick()
            waitFor("1 / 2 완료")
            compose.onNodeWithText("되돌리기").performClick()
            compose.onNodeWithText("송금 완료를 되돌릴까요?").assertIsDisplayed()
            compose.onNodeWithText("취소").performClick()
            assertEquals(1, fixtures.payments.get())
            compose.onNodeWithText("되돌리기").performClick()
            compose.onNode(hasText("되돌리기") and hasAnyAncestor(isDialog())).performClick()
            waitFor("0 / 2 완료")
            assertEquals(2, fixtures.payments.get())
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
    fun voteSubmitRemainsVisibleWhenDetailsFillTheScreen() {
        openCommunity(AfterpartyUiFixtures(partyContent = "세션 뒤풀이 안내 ".repeat(120))) {
            waitFor("테스트 뒤풀이")
            compose.onNodeWithText("테스트 뒤풀이").performClick()
            waitFor("제출하기")
            compose.onNodeWithText("제출하기").assertIsDisplayed()
        }
    }

    @Test
    fun answeredAfterpartySharesOneLoadingIndicatorWhileBothSectionsLoad() {
        val sectionsLoading = AtomicInteger()
        val sectionsResponse = CompletableDeferred<Unit>()
        val fixtures =
            AfterpartyUiFixtures(
                awaitAfterpartySections = {
                    sectionsLoading.incrementAndGet()
                    sectionsResponse.await()
                },
            )
        val progress =
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo.Indeterminate,
            )

        openCommunity(fixtures) {
            waitFor("테스트 뒤풀이")
            compose.onNodeWithText("테스트 뒤풀이").performClick()
            waitFor("제출하기")
            compose.onNodeWithText("참석").performClick()
            compose.onNodeWithText("제출하기").performClick()
            compose.waitUntil(10_000) { sectionsLoading.get() == 2 }
            compose.onAllNodes(progress).assertCountEquals(1)
            sectionsResponse.complete(Unit)
            waitFor("테스트 참석자")
            waitFor("테스트 정산자님 정산")
        }
    }

    @Test
    fun settlementOwnerSharesOneLoadingIndicatorWhileSummaryAndPeopleLoad() {
        val sectionsLoading = AtomicInteger()
        val summaryResponse = CompletableDeferred<Unit>()
        val recipientsResponse = CompletableDeferred<Unit>()
        val fixtures =
            AfterpartyUiFixtures(
                isSettlementCreator = true,
                awaitSettlementSummary = {
                    sectionsLoading.incrementAndGet()
                    summaryResponse.await()
                },
                awaitSettlementRecipients = {
                    sectionsLoading.incrementAndGet()
                    recipientsResponse.await()
                },
            )
        val progress =
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo.Indeterminate,
            )

        openCommunity(fixtures, startInCommunity = false) {
            openSettlementNotification()
            waitFor("정산 현황")
            compose.waitUntil(10_000) { sectionsLoading.get() == 2 }
            compose.onAllNodes(progress).assertCountEquals(1)

            summaryResponse.complete(Unit)
            waitFor("받을 금액")
            compose.onAllNodes(progress).assertCountEquals(1)

            recipientsResponse.complete(Unit)
            waitFor("테스트 납부 대상자")
            compose.onAllNodes(progress).assertCountEquals(0)
            compose.onNodeWithText("송금 요청 알림을 자동으로 보내요.", substring = true).assertIsDisplayed()
            val filter = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
            val unpaid = compose.onNode(filter and hasText("미송금")).fetchSemanticsNode().boundsInRoot
            val paid = compose.onNode(filter and hasText("송금 완료")).fetchSemanticsNode().boundsInRoot
            assertTrue(unpaid.left < paid.left)
        }
    }

    @Test
    fun mySettlementsShowsPayableAndReceivableFilters() {
        val fixtures = AfterpartyUiFixtures(isSettlementCreator = true, includeAnotherPayment = true)
        openCommunity(
            fixtures,
            startInCommunity = false,
            startDestination = AppNavigationDestination.MySettlements,
        ) {
            waitFor("남은 금액 8,000원")
            compose.onNodeWithText("보낼 돈").assertIsSelected()
            compose.onNodeWithText("송금 완료").performClick()
            compose.onNodeWithText("테스트 뒤풀이").assertDoesNotExist()

            compose.onNodeWithText("받을 돈").performClick()
            waitFor("0/2명 송금")
            compose.onNodeWithText("회식 정산").assertIsDisplayed()
            compose.onNodeWithText("정산 완료").performClick()
            compose.onNodeWithText("테스트 뒤풀이").assertDoesNotExist()
            val receivingFilter =
                SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and hasText("받는 중")
            compose.onNode(receivingFilter).performClick()
            compose.onNodeWithText("테스트 뒤풀이").performClick()
            waitFor("내 정산")
            compose.onNodeWithText("받을 금액").assertIsDisplayed()
        }
    }

    @Test
    fun settlementNotificationAnnouncesItsActualDestination() {
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
        val opensSettlement =
            SemanticsMatcher("opens settlement details") {
                it.config.getOrNull(SemanticsActions.OnClick)?.label == "정산 상세 보기"
            }
        compose.onNode(opensSettlement).assertIsDisplayed().performClick()
    }

    private fun openCommunity(
        fixtures: AfterpartyUiFixtures,
        startInCommunity: Boolean = true,
        startDestination: AppNavigationDestination = AppNavigationDestination.Main,
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
                    YappTheme { AppNavHost(onExit = {}, startDestination = startDestination) }
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
    private val partyContent: String = "세션 뒤풀이 안내",
    private val isSettlementCreator: Boolean = false,
    private val awaitAfterpartySections: suspend () -> Unit = {},
    private val awaitSettlementSummary: suspend () -> Unit = {},
    private val awaitSettlementRecipients: suspend () -> Unit = {},
    private val includeEndedAfterparty: Boolean = false,
    private val includeAnotherPayment: Boolean = false,
    private val eventEnded: Boolean = false,
    private val awaitAfterparties: suspend () -> Unit = {},
) {
    val votes = AtomicInteger()
    val attendeeReads = AtomicInteger()
    val payments = AtomicInteger()
    val createdSettlement = AtomicReference<JsonObject?>()
    private val paid = AtomicBoolean()

    fun client(): HttpClient =
        HttpClient(
            MockEngine { request ->
                val path = request.url.encodedPath
                val isCreateSettlement =
                    path.endsWith("/afterparties/9/settlements") && request.method == HttpMethod.Post
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
                            val filter = request.url.parameters["filter"]
                            if (filter == "ALL") awaitAfterparties()
                            afterpartyList(filter)
                        }
                        path.endsWith("/afterparties/9/response") -> {
                            assertEquals(HttpMethod.Put, request.method)
                            assertEquals("""{"response":"ATTENDING"}""", (request.body as TextContent).text)
                            votes.incrementAndGet()
                            """{"response":"ATTENDING"}"""
                        }
                        path.endsWith("/afterparties/9/attendees") -> {
                            awaitAfterpartySections()
                            attendees()
                        }
                        path.endsWith("/afterparties/9/settlement-participants") ->
                            """
                            {"items":[
                              {"recipientId":31,"name":"테스트 참석자","teamName":"1팀",
                               "position":"ANDROID","isMe":false},
                              {"recipientId":32,"name":"제외할 참석자","teamName":"2팀",
                               "position":"DESIGN","isMe":false}
                            ],"nextCursor":null}
                            """.trimIndent()
                        isCreateSettlement -> {
                            val payload = (request.body as TextContent).text
                            val request = Json.parseToJsonElement(payload).jsonObject
                            createdSettlement.set(request)
                            val count = request.getValue("recipientIds").jsonArray.size
                            settlementDetail().replace("\"debtorCount\":2", "\"debtorCount\":$count")
                        }
                        path.endsWith("/afterparties/9/settlements") -> {
                            if (votes.get() > 0) awaitAfterpartySections()
                            val items =
                                buildList {
                                    add(settlementItem())
                                    if (includeAnotherPayment) add(additionalSettlementItem())
                                }
                            """{"items":[${items.joinToString()}],"nextCursor":null}"""
                        }
                        path.endsWith("/afterparties/9/settlements/13/payment") -> {
                            assertEquals(HttpMethod.Put, request.method)
                            val expectedStatus = if (paid.get()) "UNPAID" else "PAID"
                            val result = """{"paymentStatus":"$expectedStatus"}"""
                            assertEquals(result, (request.body as TextContent).text)
                            payments.incrementAndGet()
                            paid.set(expectedStatus == "PAID")
                            result
                        }
                        path.endsWith("/afterparties/9/settlements/13/summary") -> {
                            awaitSettlementSummary()
                            """{"expectedReceivableAmount":24000,"perPersonAmount":12000,"debtorCount":2,"paidCount":0}"""
                        }
                        path.endsWith("/afterparties/9/settlements/13/recipients") -> {
                            awaitSettlementRecipients()
                            """
                            {"items":[{"recipientId":31,"name":"테스트 납부 대상자","teamName":null,
                              "position":null,"isMe":false}],"nextCursor":null}
                            """.trimIndent()
                        }
                        path.endsWith("/afterparties/9/settlements/13") -> settlementDetail()
                        path.endsWith("/afterparties/9/settlements/14") -> settlementDetail(additionalSettlementItem())
                        path.endsWith("/afterparties/9") -> afterpartyDetail()
                        else -> error("Unexpected mock path $path")
                    }
                respond(
                    body,
                    status = if (isCreateSettlement) HttpStatusCode.Created else HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
        ) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private fun afterpartyDetail(): String {
        val response = if (votes.get() == 0) "UNANSWERED" else "ATTENDING"
        val count = if (votes.get() == 0) "null" else "1"
        val state = if (eventEnded) "ENDED" else "OPEN"
        return """
            {"afterpartyId":9,"title":"테스트 뒤풀이","content":"$partyContent",$schedule,
             "state":"$state","response":"$response","attendingCount":$count}
            """.trimIndent()
    }

    private fun afterpartyList(filter: String?): String {
        val items =
            buildList {
                if (filter != "CLOSED") add(openAfterparty)
                if (includeEndedAfterparty && filter != "OPEN") add(endedAfterparty)
            }
        return """{"items":[${items.joinToString()}],"nextCursor":null}"""
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
                isSettlementCreator -> "null"
                paid.get() -> "\"PAID\""
                else -> "\"UNPAID\""
            }
        return """
            {"settlementId":13,"afterpartyId":9,"creatorName":"테스트 정산자","isMe":$isSettlementCreator,
             "participantCount":3,"myAmount":12000,"paymentStatus":$status}
            """.trimIndent()
    }

    private fun additionalSettlementItem(): String =
        """
        {"settlementId":14,"afterpartyId":9,"creatorName":"다른 정산자","isMe":false,
         "participantCount":3,"myAmount":8000,"paymentStatus":"UNPAID"}
        """.trimIndent()

    private fun settlementDetail(item: String = settlementItem()): String =
        """
        ${item.dropLast(1)},"totalAmount":36000,"perPersonAmount":12000,
         "bank":"TOSS","accountNumber":"001-234","accountHolder":"테스트 정산자",
         "debtorCount":2,"paidCount":0,"recipientIds":[],"photos":[],"revision":0}
        """.trimIndent()

    private val schedule =
        """
        "startsAt":"2030-03-23T09:30:00Z","startsAtDisplay":"03.23 [토] 18:30",
        "deadline":"2030-03-22T14:59:00Z","deadlineDisplay":"03.22 [금] 23:59"
        """.trimIndent()

    private val openAfterparty =
        """
        {"afterpartyId":9,"title":"테스트 뒤풀이",$schedule,"respondedCount":42,"state":"OPEN"}
        """.trimIndent()

    private val endedAfterparty =
        """
        {"afterpartyId":8,"title":"지난 뒤풀이",
         "startsAt":"2026-03-21T09:30:00Z","startsAtDisplay":"03.21 [토] 18:30",
         "deadline":"2026-03-20T14:59:00Z","deadlineDisplay":"03.20 [금] 23:59",
         "respondedCount":32,"state":"ENDED"}
        """.trimIndent()
}
