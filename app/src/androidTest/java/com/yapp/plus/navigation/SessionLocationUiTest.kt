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
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.swipeDown
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.di.AuthE2ETestEntryPoint
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.SessionType
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
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

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
    fun sessionWithCoordinatesShowsVenueAndCodeAttendanceWithoutDistanceActions() {
        installApiFixtures(includeLocation = true).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText(ROAD_ADDRESS, substring = true)
            composeRule.onNodeWithText(ROAD_ADDRESS, substring = true).assertIsDisplayed()
            composeRule.onNodeWithText(FULL_ADDRESS, substring = true).assertDoesNotExist()
            composeRule.onNodeWithText(CHECK_DISTANCE).assertDoesNotExist()
            composeRule.onNodeWithText("거리 다시 확인").assertDoesNotExist()
            composeRule.onNodeWithText("출석하기").assertIsEnabled().performClick()
            composeRule.onNodeWithText("출석 코드를 입력해 주세요").assertIsDisplayed()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            composeRule.onNodeWithText("일정").performClick()
            waitForText(FULL_ADDRESS, substring = true)
        }
    }

    @Test
    fun codeAttendanceRejectsInvalidLocationBeforeSendingVerification() {
        val phase = InstrumentationRegistry.getArguments().getString("sessionLocationPhase")
        val locationError =
            when (phase) {
                "precise" -> "세션 장소에서 100m 이내로 이동한 뒤 다시 시도해 주세요."
                "denied" -> "출석하려면 위치 권한을 허용해 주세요."
                "disabled" -> "휴대폰의 위치 기능을 켜 주세요"
                else -> null
            }
        assumeTrue(locationError != null)
        val verifications = AtomicInteger()
        installApiFixtures(
            includeLocation = true,
            onVerification = { verifications.incrementAndGet() },
        ).use {
            assertLocationPermission(phase)
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석하기")
            composeRule.onNodeWithText("출석하기").assertIsEnabled().performClick()
            composeRule.onNode(hasSetTextAction()).performTextInput("1234")
            composeRule.onNodeWithText("확인").performClick()
            waitForText(requireNotNull(locationError))
            composeRule.onNodeWithText("확인").assertIsEnabled()
            assertEquals(0, verifications.get())
        }
    }

    @Test
    fun existingSessionKeepsItsVenueAndCodeAttendance() {
        installApiFixtures(
            includeLocation = false,
            weeklySessionDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1),
        ).use {
            composeRule.setContent {
                YappTheme { MainTabScaffold() }
            }
            waitForText("DMC 첨단산업센터")
            composeRule.onNodeWithText(CHECK_DISTANCE).assertDoesNotExist()
            composeRule.onNodeWithText(REPORT_TIP).assertDoesNotExist()
            composeRule.onNodeWithText("출석하기").assertIsEnabled()
            composeRule.onNodeWithText("일정").performClick()
            waitForText("DMC 첨단산업센터")
            composeRule.onNodeWithText("-").assertIsDisplayed()
        }
    }

    @Test
    fun codeRequiresFourDigitsAndSuccessShowsCompletedCard() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("sessionLocationPhase") == "precise")
        val verifications = AtomicInteger()
        val completionScreenCount = AtomicInteger()
        installApiFixtures(
            includeLocation = true,
            venueLatitude = MOCK_LOCATION_LATITUDE,
            onVerification = { verifications.incrementAndGet() },
        ).use {
            composeRule.setContent {
                YappTheme {
                    MainTabScaffold(onAttendanceComplete = { completionScreenCount.incrementAndGet() })
                }
            }
            waitForText("출석하기")
            composeRule.onNodeWithText("출석하기").performClick()
            composeRule.onNodeWithText("확인").assertIsNotEnabled()
            composeRule.onNode(hasSetTextAction()).performTextInput("123")
            composeRule.onNodeWithText("확인").assertIsNotEnabled()
            composeRule.onNode(hasSetTextAction()).performTextInput("4")
            composeRule.onNodeWithText("확인").assertIsEnabled().performClick()
            waitForText("출석했어요!")
            composeRule.onNodeWithText("출석했어요!").assertIsNotEnabled()
            assertEquals(1, verifications.get())
            composeRule.runOnIdle { assertEquals(1, completionScreenCount.get()) }
        }
    }

    @Test
    fun missingVenueBlocksSubmissionAndKeepsCode() {
        val verifications = AtomicInteger()
        installApiFixtures(
            includeLocation = false,
            onVerification = { verifications.incrementAndGet() },
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석하기")
            composeRule.onNodeWithText("출석하기").performClick()
            composeRule.onNode(hasSetTextAction()).performTextInput("1234")
            composeRule.onNodeWithText("확인").performClick()
            waitForText("세션 위치를 확인할 수 없어요.", substring = true)
            composeRule.onNodeWithText("세션 다시 확인").assertIsEnabled()
            composeRule.onNodeWithText("확인").assertIsEnabled()
            assertEquals(0, verifications.get())
        }
    }

    @Test
    fun oldLocationCannotBeReusedForAttendance() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("sessionLocationPhase") == "stale")
        val verifications = AtomicInteger()
        installApiFixtures(
            includeLocation = true,
            venueLatitude = MOCK_LOCATION_LATITUDE,
            onVerification = { verifications.incrementAndGet() },
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석하기")
            composeRule.onNodeWithText("출석하기").performClick()
            composeRule.onNode(hasSetTextAction()).performTextInput("1234")
            composeRule.onNodeWithText("확인").performClick()
            waitForText("위치 확인 시간이 지났어요. 다시 시도해 주세요.")
            composeRule.onNodeWithText("확인").assertIsEnabled()
            assertEquals(0, verifications.get())
        }
    }

    @Test
    fun serverCheckedInShowsCompletedCardWithoutOpeningCompletionScreen() {
        installApiFixtures(
            includeLocation = false,
            currentAttendanceStatus = { AttendanceStatus.CHECKED_IN },
        ).use {
            var completionScreenCount = 0
            composeRule.setContent {
                YappTheme { MainTabScaffold(onAttendanceComplete = { completionScreenCount++ }) }
            }
            waitForText("출석했어요!")
            composeRule.onNodeWithText("출석했어요!").assertIsNotEnabled()
            composeRule.onNodeWithText("출석").assertIsDisplayed()
            composeRule.runOnIdle { assertEquals(0, completionScreenCount) }
            composeRule.onNodeWithText("출석하기").assertDoesNotExist()
            composeRule.onNodeWithText("출석 점수 확인하기").assertDoesNotExist()
        }
    }

    @Test
    fun pendingApprovalBlocksCodeAndRefreshesWhenHomeResumes() {
        val attendanceStatus = AtomicReference(AttendanceStatus.PENDING_APPROVAL)
        installApiFixtures(
            includeLocation = false,
            currentAttendanceStatus = attendanceStatus::get,
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석 확인 중이에요")
            composeRule.onNodeWithText("출석 확인 중이에요").assertIsNotEnabled()
            composeRule.onNodeWithText("출석하기").assertDoesNotExist()
            composeRule.onNodeWithText("출석 점수 확인하기").assertDoesNotExist()

            val scenario = composeRule.activityRule.scenario
            scenario.moveToState(Lifecycle.State.CREATED)
            attendanceStatus.set(AttendanceStatus.CHECKED_IN)
            scenario.moveToState(Lifecycle.State.RESUMED)

            waitForText("출석했어요!")
            composeRule.onNodeWithText("출석 확인 중이에요").assertDoesNotExist()
            composeRule.onNodeWithText("출석했어요!").assertIsNotEnabled()
        }
    }

    @Test
    fun checkedInTeamSessionWithoutLocationShowsTeamCard() {
        installApiFixtures(
            includeLocation = false,
            currentAttendanceStatus = { AttendanceStatus.CHECKED_IN },
            weeklySessionType = SessionType.TEAM,
            weeklySessionName = "반려된 퇴근",
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            val approvalTitle = "팀 세션 출석이 완료됐어요!"
            waitForText(approvalTitle)
            composeRule.onNodeWithText("확인").performClick()
            composeRule.onNodeWithText("이번 주는 반려된 퇴근 세션이에요").assertIsDisplayed()
            composeRule.onNodeWithText("출석했어요!").assertIsNotEnabled()
        }
    }

    @Test
    fun teamApprovalAfterRefreshRemembersConfirmationAcrossTabs() {
        val attendanceStatus = AtomicReference(AttendanceStatus.PENDING_APPROVAL)
        val teamPhotoLookups = AtomicInteger()
        installApiFixtures(
            includeLocation = false,
            currentAttendanceStatus = attendanceStatus::get,
            weeklySessionType = SessionType.TEAM,
            onTeamPhotoLookup = { teamPhotoLookups.incrementAndGet() },
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석 확인 중이에요")

            val initialLookups = teamPhotoLookups.get()
            composeRule.onNode(hasScrollAction()).performTouchInput { swipeDown() }
            composeRule.waitUntil(10_000) { teamPhotoLookups.get() > initialLookups }
            waitForText("출석 확인 중이에요")

            attendanceStatus.set(AttendanceStatus.CHECKED_IN)
            composeRule.onNode(hasScrollAction()).performTouchInput { swipeDown() }

            val approvalTitle = "팀 세션 출석이 완료됐어요!"
            waitForText(approvalTitle)
            composeRule.onNodeWithText(approvalTitle).assertIsDisplayed()
            composeRule.onNodeWithText("확인").performClick()
            composeRule.onNodeWithText(approvalTitle).assertDoesNotExist()
            composeRule.onNodeWithText("출석했어요!").assertIsNotEnabled()

            composeRule.onNodeWithText("일정").performClick()
            composeRule.onNodeWithText("홈").performClick()
            waitForText("출석했어요!")
            composeRule.onNodeWithText(approvalTitle).assertDoesNotExist()
        }
    }

    @Test
    fun reportConfirmRequiresNonBlankReasonBeforeSubmitting() {
        val reportBody = AtomicReference<String>()
        installApiFixtures(
            includeLocation = false,
            weeklySessionDate = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(1),
            onReport = reportBody::set,
        ).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("출석하기")
            composeRule.onNodeWithText("출결 보고").performClick()
            composeRule.onNodeWithText("지각").performClick()
            val confirm = composeRule.onNodeWithText("확인")
            confirm.assertIsEnabled().performClick()
            confirm.assertIsNotEnabled()

            val reason = composeRule.onNode(hasSetTextAction())
            reason.performTextInput("   ")
            confirm.assertIsNotEnabled()
            assertNull(reportBody.get())

            reason.performTextReplacement("  개인 일정  ")
            confirm.assertIsEnabled().performClick()
            waitForText("출결 보고를 완료했어요")
            assertEquals(
                Json.parseToJsonElement("""{"type":"LATE","reason":"개인 일정"}"""),
                Json.parseToJsonElement(reportBody.get()),
            )
        }
    }

    @Test
    fun inactiveGenerationShowsNormalMessageAndBlocksReport() {
        installApiFixtures(includeLocation = false, inactive = true).use {
            composeRule.setContent { YappTheme { MainTabScaffold() } }
            waitForText("현재 활동 기간이 아니에요.")
            composeRule.onNodeWithText(REPORT_TIP).assertDoesNotExist()
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
        venueLatitude: Double = VENUE_LATITUDE,
        onVerification: () -> Unit = {},
        currentAttendanceStatus: () -> AttendanceStatus = { AttendanceStatus.AVAILABLE },
        weeklySessionDate: LocalDate = LocalDate.of(2026, 11, 14),
        onReport: (String) -> Unit = { error("Unexpected attendance report") },
        weeklySessionType: SessionType = SessionType.REGULAR,
        weeklySessionName: String = "개발 세션",
        onTeamPhotoLookup: () -> Unit = {},
    ): HttpClient {
        val client =
            HttpClient(
                MockEngine { request ->
                    val response =
                        when (request.url.encodedPath.substringAfterLast('/')) {
                            "current-week" ->
                                weeklySession(
                                    includeLocation,
                                    venueLatitude,
                                    currentAttendanceStatus(),
                                    weeklySessionDate,
                                    weeklySessionType,
                                    weeklySessionName,
                                )
                            "sessions" -> generationSessions(includeLocation)
                            "team-attendance-photos" -> {
                                onTeamPhotoLookup()
                                """{"photo":null}"""
                            }
                            "attendance-verifications" -> {
                                onVerification()
                                """{"verified":true,"checkedInAt":"2026-11-18T05:00:00Z"}"""
                            }
                            "attendance-reports" -> {
                                onReport((request.body as TextContent).text)
                                """{"sessionId":12,"submittedAt":"2026-11-18T05:00:00Z"}"""
                            }
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

    private fun weeklySession(
        includeLocation: Boolean,
        venueLatitude: Double,
        attendanceStatus: AttendanceStatus,
        sessionDate: LocalDate,
        sessionType: SessionType,
        sessionName: String,
    ): String {
        val locationDetails = if (includeLocation) locationDetails(venueLatitude) else ""
        val location = if (sessionType == SessionType.TEAM) "null" else "\"DMC 첨단산업센터\""
        return """{"weekStartDate":"${sessionDate.minusDays(6)}","weekEndDate":"$sessionDate",
            "session":{"sessionId":12,"name":${JsonPrimitive(sessionName)},"date":"$sessionDate",
            "startTime":"14:00","endTime":"18:00","type":"${sessionType.name}","location":$location,
            "attendanceStatus":"${attendanceStatus.name}"$locationDetails}}"""
    }

    private fun generationSessions(includeLocation: Boolean): String {
        val locationDetails = if (includeLocation) locationDetails(VENUE_LATITUDE) else ""
        return """{"generationId":31,"generationNumber":31,"sessions":[
            {"sessionId":12,"name":"개발 세션","date":"2026-11-14","dayOfWeek":"SATURDAY",
            "startTime":"14:00","endTime":"18:00","type":"REGULAR",
            "location":"DMC 첨단산업센터"$locationDetails},
            {"sessionId":13,"name":"팀 세션","date":"2026-11-21","dayOfWeek":"SATURDAY",
            "startTime":"14:00","endTime":"18:00","type":"TEAM"}]}"""
    }

    private fun assertLocationPermission(phase: String?) {
        val hasFine = context.checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
        if (phase == "denied") {
            val hasCoarse = context.checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            check(!hasFine && !hasCoarse)
        } else {
            check(hasFine)
        }
    }

    private fun waitForText(
        text: String,
        substring: Boolean = false,
    ) {
        try {
            composeRule.waitUntil(20_000) {
                composeRule
                    .onAllNodesWithText(text, substring)
                    .fetchSemanticsNodes(atLeastOneRootRequired = false)
                    .isNotEmpty()
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
        const val REPORT_TIP = "세션 시작 전에\n미리 보고해 주세요"
        const val ROAD_ADDRESS = "서울특별시 마포구 월드컵북로 396"
        const val FULL_ADDRESS = "$ROAD_ADDRESS 2층"
        const val VENUE_LATITUDE = 37.58
        const val MOCK_LOCATION_LATITUDE = 37.581

        fun locationDetails(latitude: Double): String =
            ""","roadAddress":"서울특별시 마포구 월드컵북로 396","detailAddress":"2층",
            "latitude":$latitude,"longitude":126.88"""
    }
}
