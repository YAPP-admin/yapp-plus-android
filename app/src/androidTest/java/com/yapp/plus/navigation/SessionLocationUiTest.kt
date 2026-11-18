package com.yapp.plus.navigation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.view.KeyEvent
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToString
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
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class SessionLocationUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<NavigationTestActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @After
    fun clearFixture() {
        ApiFixtures.clear()
    }

    @Test
    fun distanceFlowKeepsCodeAttendanceAvailableWithAndWithoutPermission() {
        val phase = InstrumentationRegistry.getArguments().getString("sessionLocationPhase")
        assumeTrue(phase in listOf("precise", "approximate", "denied", "disabled", "unavailable"))
        installApiFixtures(includeLocation = true).use {
            assertLocationPermission(phase)
            composeRule.setContent {
                YappTheme { MainTabScaffold() }
            }
            waitForText(CHECK_DISTANCE)
            composeRule.onNodeWithText(ADDRESS, substring = true).assertIsDisplayed()
            composeRule.onNodeWithText("출석하기").assertIsEnabled()

            composeRule.onNodeWithText(CHECK_DISTANCE).performClick()
            assertLocationResult(phase)

            composeRule.onNodeWithText("출석하기").assertIsEnabled().performClick()
            composeRule.onNodeWithText("출석 코드를 입력해 주세요").assertIsDisplayed()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            composeRule.onNodeWithText("일정").performClick()
            waitForText(ADDRESS, substring = true)
        }
    }

    @Test
    fun existingSessionKeepsItsVenueAndCodeAttendance() {
        installApiFixtures(includeLocation = false).use {
            composeRule.setContent {
                YappTheme { MainTabScaffold() }
            }
            waitForText("DMC 첨단산업센터")
            composeRule.onNodeWithText(CHECK_DISTANCE).assertDoesNotExist()
            composeRule.onNodeWithText("출석하기").assertIsEnabled()
            composeRule.onNodeWithText("일정").performClick()
            waitForText("DMC 첨단산업센터")
            composeRule.onNodeWithText("-").assertIsDisplayed()
        }
    }

    @Test
    fun codeRequiresFourDigitsAndSuccessOffersMyScore() {
        installApiFixtures(includeLocation = false).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석하기")
            composeRule.onNodeWithText("출석하기").performClick()
            composeRule.onNodeWithText("확인").assertIsNotEnabled()
            composeRule.onNode(hasSetTextAction()).performTextInput("123")
            composeRule.onNodeWithText("확인").assertIsNotEnabled()
            composeRule.onNode(hasSetTextAction()).performTextInput("4")
            composeRule.onNodeWithText("확인").assertIsEnabled().performClick()
            waitForText("출석 점수 확인하기")
            composeRule.onNodeWithText("출석 점수 확인하기").performClick()
            waitForText("마이페이지")
        }
    }

    @Test
    fun inactiveGenerationShowsNormalMessageAndBlocksReport() {
        installApiFixtures(includeLocation = false, inactive = true).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("현재 활동 기간이 아니에요.")
            composeRule.onNodeWithText("출결 보고").performClick()
            composeRule.onNodeWithText("결석").performClick()
            composeRule.onNodeWithText("확인").assertIsNotEnabled()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            composeRule.onNodeWithText("일정").performClick()
            waitForText("현재 활동 기간이 아니에요.")
        }
    }

    private fun installApiFixtures(
        includeLocation: Boolean,
        inactive: Boolean = false,
    ): HttpClient {
        val client =
            HttpClient(
                MockEngine { request ->
                    val response =
                        when (request.url.encodedPath.substringAfterLast('/')) {
                            "current-week" -> weeklySession(includeLocation)
                            "sessions" -> generationSessions(includeLocation)
                            "attendance-verifications" ->
                                """{"verified":true,"checkedInAt":"2026-11-18T05:00:00Z"}"""
                            "me" ->
                                if (request.url.encodedPath.contains("attendance-scores")) {
                                    """{"generationId":31,"totalScore":100,"attendanceCount":1,
                                "lateCount":0,"absentCount":0,"earlyLeaveCount":0}"""
                                } else {
                                    """{"userId":1,"name":"테스트"}"""
                                }
                            "posts" -> """{"totalPostCount":0,"items":[],"nextCursor":null}"""
                            else -> error("Unexpected path ${request.url.encodedPath}")
                        }
                    val noGeneration =
                        inactive &&
                            request.url.encodedPath.substringAfterLast('/') in
                            listOf("current-week", "sessions")
                    respond(
                        if (noGeneration) """{"code":"CURRENT_GENERATION_NOT_FOUND"}""" else response,
                        status =
                            if (noGeneration) {
                                io.ktor.http.HttpStatusCode.NotFound
                            } else {
                                io.ktor.http.HttpStatusCode.OK
                            },
                        headers =
                            headersOf(
                                HttpHeaders.ContentType,
                                ContentType.Application.Json.toString(),
                            ),
                    )
                },
            ) {
                install(ContentNegotiation) { json() }
            }
        val entryPoint =
            EntryPointAccessors.fromApplication(context, AuthE2ETestEntryPoint::class.java)
        ApiFixtures.install(entryPoint.authenticatedClient(), client)
        return client
    }

    private fun weeklySession(includeLocation: Boolean): String {
        val locationDetails = if (includeLocation) LOCATION_DETAILS else ""
        return """{"weekStartDate":"2026-11-08","weekEndDate":"2026-11-14",
            "session":{"sessionId":12,"name":"개발 세션","date":"2026-11-14","startTime":"14:00",
            "location":"DMC 첨단산업센터","attendanceStatus":"AVAILABLE"$locationDetails}}"""
    }

    private fun generationSessions(includeLocation: Boolean): String {
        val locationDetails = if (includeLocation) LOCATION_DETAILS else ""
        return """{"generationId":31,"generationNumber":31,"sessions":[
            {"sessionId":12,"name":"개발 세션","date":"2026-11-14","dayOfWeek":"SATURDAY",
            "startTime":"14:00","endTime":"18:00","type":"REGULAR",
            "location":"DMC 첨단산업센터"$locationDetails},
            {"sessionId":13,"name":"팀 세션","date":"2026-11-21","dayOfWeek":"SATURDAY",
            "startTime":"14:00","endTime":"18:00","type":"TEAM"}]}"""
    }

    private fun assertLocationResult(phase: String?) {
        when (phase) {
            "precise" -> waitForText("세션 장소까지 약 111 m")
            "approximate" -> {
                waitForText("대략적인 위치로 계산했어요", substring = true)
                waitForText("세션 장소까지 약", substring = true)
            }
            "denied" -> {
                waitForText("거리 확인에는 위치 권한이 필요해요")
                composeRule.onNodeWithText("위치 설정 열기").assertIsEnabled()
            }
            "disabled" -> waitForText("휴대폰의 위치 기능을 켜 주세요")
            "unavailable" -> {
                waitForText("현재 위치를 확인하지 못했어요. 다시 시도해 주세요")
                composeRule.onNodeWithText("다시 시도").assertIsEnabled()
            }
        }
    }

    private fun assertLocationPermission(phase: String?) {
        val hasFine = context.checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
        val hasCoarse = context.checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        when (phase) {
            "denied" -> check(!hasFine && !hasCoarse)
            "approximate" -> check(!hasFine && hasCoarse)
            else -> check(hasFine)
        }
    }

    private fun waitForText(
        text: String,
        substring: Boolean = false,
    ) {
        try {
            composeRule.waitUntil(20_000) {
                composeRule.onAllNodesWithText(text, substring).fetchSemanticsNodes().isNotEmpty()
            }
        } catch (exception: ComposeTimeoutException) {
            val tree = composeRule.onRoot().printToString()
            throw AssertionError("Missing text $text\n$tree", exception)
        }
    }

    private object ApiFixtures {
        @Volatile
        private var currentClient: HttpClient? = null

        private var interceptedClient: HttpClient? = null

        fun install(
            authenticatedClient: HttpClient,
            fixtureClient: HttpClient,
        ) {
            currentClient = fixtureClient
            if (interceptedClient == authenticatedClient) return
            interceptedClient = authenticatedClient
            authenticatedClient.plugin(HttpSend).intercept { request ->
                val client = currentClient ?: return@intercept execute(request)
                client.request(request).call
            }
        }

        fun clear() {
            currentClient = null
        }
    }

    private companion object {
        const val CHECK_DISTANCE = "세션 장소까지 거리 확인"
        const val ADDRESS = "서울특별시 마포구 월드컵북로 396 2층"
        const val LOCATION_DETAILS =
            ""","roadAddress":"서울특별시 마포구 월드컵북로 396","detailAddress":"2층",
            "latitude":37.58,"longitude":126.88"""
    }
}
