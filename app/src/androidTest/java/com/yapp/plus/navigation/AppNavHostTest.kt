package com.yapp.plus.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class AppNavHostTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<NavigationTestActivity>()

    private lateinit var navController: NavHostController
    private lateinit var restorationTester: StateRestorationTester
    private var hasExited = false
    private val authenticated = mutableStateOf<Boolean?>(null)
    private val pushPostId = mutableStateOf<Long?>(null)
    private val pushSessionId = mutableStateOf<Long?>(null)

    @Before
    fun setUp() {
        restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                navController = rememberNavController()
                AppNavHost(
                    onExit = { hasExited = true },
                    navController = navController,
                    sessionAuthenticated = authenticated.value,
                    pushPostId = pushPostId.value,
                    onPushPostOpened = { pushPostId.value = null },
                    pushSessionId = pushSessionId.value,
                    onPushSessionOpened = { pushSessionId.value = null },
                )
            }
        }
    }

    @Test
    fun startsAtLoginAndPreservesItAcrossRestoration() {
        assertCurrentRoute<AppNavigationDestination.Login>()
        restorationTester.emulateSavedInstanceStateRestore()
        assertCurrentRoute<AppNavigationDestination.Login>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
        Espresso.pressBack()
        composeRule.runOnIdle { assertTrue(hasExited) }
    }

    @Test
    fun repeatedForwardCallbacksDoNotDuplicateDestinations() {
        assertCurrentRoute<AppNavigationDestination.Login>()
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.AttendanceComplete)

        popAndAssert<AppNavigationDestination.SignUp>()
        popAndAssert<AppNavigationDestination.Login>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
    }

    @Test
    fun repeatedBackCallbacksPopOnlyTheirOwnEntry() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.AttendanceComplete)

        composeRule.runOnIdle {
            val pendingEntry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateBackFrom(pendingEntry, onExit = {}) }
        }

        assertCurrentRoute<AppNavigationDestination.SignUp>()
    }

    @Test
    fun restoresTheDestinationAndCompleteBackStack() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.AttendanceComplete)

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AppNavigationDestination.AttendanceComplete>()
        popAndAssert<AppNavigationDestination.SignUp>()
        popAndAssert<AppNavigationDestination.Login>()
    }

    @Test
    fun opensInboxFromHomeAndReturnsToItFromTheNoticeDetail() {
        withInboxFixtures {
            openInbox()
            navigateRepeatedly(AppNavigationDestination.NotificationPost(postId = 42))
            assertCurrentRoute<AppNavigationDestination.NotificationPost>()
            composeRule.runOnIdle {
                val route =
                    checkNotNull(navController.currentBackStackEntry)
                        .toRoute<AppNavigationDestination.NotificationPost>()
                assertEquals(42L, route.postId)
            }
            popAndAssert<AppNavigationDestination.Notifications>()
            popAndAssert<AppNavigationDestination.Main>()
        }
    }

    @Test
    fun opensTheMatchingScheduleSessionAndReturnsToTheInbox() {
        withInboxFixtures {
            openInbox()
            waitForText("이번 주 세션 알림")
            composeRule.onNodeWithText("이번 주 세션 알림").performClick()
            assertCurrentRoute<AppNavigationDestination.NotificationSchedule>()
            waitForText("테스트 세션 71")
            composeRule.onNodeWithText("테스트 세션 71").assertIsDisplayed()
            popAndAssert<AppNavigationDestination.Notifications>()
            waitForText("이번 주 세션 알림")
        }
    }

    private fun openInbox() {
        composeRule.runOnIdle { authenticated.value = true }
        assertCurrentRoute<AppNavigationDestination.Main>()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithContentDescription("알림").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("알림").performClick()
        assertCurrentRoute<AppNavigationDestination.Notifications>()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(10_000) { composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun withInboxFixtures(block: () -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val entryPoint = EntryPointAccessors.fromApplication(context, AuthE2ETestEntryPoint::class.java)
        val engine =
            MockEngine { request ->
                val body =
                    when (request.url.encodedPath.substringAfterLast('/')) {
                        "current-week" ->
                            """{"weekStartDate":"2026-11-22","weekEndDate":"2026-11-28","session":null}"""
                        "posts" -> """{"totalPostCount":0,"items":[],"nextCursor":null}"""
                        "notifications" ->
                            """
                            {"items":[{"id":7,"content":"이번 주 세션 알림","sentDate":"2026-11-25",
                             "type":"SESSION_REMINDER","sessionId":71,"postId":null,
                             "navigationTarget":"SESSION_SCHEDULE"}],"nextCursor":null}
                            """.trimIndent()
                        "sessions" -> scheduleFixture()
                        else -> "{}"
                    }
                respond(body, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            }
        HttpClient(engine) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }.use { fake ->
            val active = AtomicReference<HttpClient?>(fake)
            entryPoint.authenticatedClient().plugin(HttpSend).intercept { request ->
                val client = active.get() ?: return@intercept execute(request)
                client.request(request).call
            }
            try {
                block()
            } finally {
                active.set(null)
            }
        }
    }

    private fun scheduleFixture(): String {
        val sessions =
            (48..71).joinToString { id ->
                val date = LocalDate.of(2026, 1, 3).plusWeeks((id - 48).toLong())
                """
                {"sessionId":$id,"name":"테스트 세션 $id","date":"$date","dayOfWeek":"SATURDAY",
                 "startTime":"14:00","endTime":"18:00","type":"TEAM","location":null}
                """.trimIndent()
            }
        return """{"generationId":31,"generationNumber":31,"sessions":[$sessions]}"""
    }

    @Test
    fun waitsForLoginBeforeOpeningThePushNotice() {
        composeRule.runOnIdle {
            authenticated.value = false
            pushPostId.value = 42
        }
        assertCurrentRoute<AppNavigationDestination.Login>()

        composeRule.runOnIdle { authenticated.value = true }

        assertCurrentRoute<AppNavigationDestination.NotificationPost>()
        composeRule.runOnIdle {
            val route =
                checkNotNull(navController.currentBackStackEntry)
                    .toRoute<AppNavigationDestination.NotificationPost>()
            assertEquals(42L, route.postId)
            assertEquals(null, pushPostId.value)
        }
        popAndAssert<AppNavigationDestination.Main>()
    }

    @Test
    fun waitsForLoginBeforeOpeningThePushSchedule() {
        composeRule.runOnIdle {
            authenticated.value = false
            pushSessionId.value = 71
        }
        assertCurrentRoute<AppNavigationDestination.Login>()

        composeRule.runOnIdle { authenticated.value = true }

        assertCurrentRoute<AppNavigationDestination.NotificationSchedule>()
        composeRule.runOnIdle {
            val route =
                checkNotNull(navController.currentBackStackEntry)
                    .toRoute<AppNavigationDestination.NotificationSchedule>()
            assertEquals(71L, route.sessionId)
            assertEquals(null, pushSessionId.value)
        }
        popAndAssert<AppNavigationDestination.Main>()
    }

    @Test
    fun signupDoesNotCompleteWithoutServerAuthentication() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        composeRule.onNodeWithText("다음").assertIsNotEnabled()
        updateInput("이름")
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        updateInput("01012345678", actionText = "회원가입")
        composeRule.onNodeWithText("회원가입").assertIsNotEnabled()
        composeRule.onNodeWithText("회원가입을 완료했어요!").assertDoesNotExist()
        assertCurrentRoute<AppNavigationDestination.SignUp>()
    }

    @Test
    fun preservesInputsAcrossRestorationAndNavigationBack() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        updateInput("이름")
        composeRule.onNodeWithText("다음").performClick()
        updateInput("01012345678", actionText = "회원가입")

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AppNavigationDestination.SignUp>()
        composeRule.onNode(hasSetTextAction()).assertTextContains("010-1234-5678")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextContains("이름")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        navigateRepeatedly(AppNavigationDestination.SignUp)
        composeRule.onNode(hasSetTextAction()).assertTextContains("이름")
    }

    private fun updateInput(
        value: String,
        actionText: String = "다음",
    ) {
        val input = composeRule.onNode(hasSetTextAction())
        input.performTextInput(value)
        input.performImeAction()
        Espresso.closeSoftKeyboard()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == 1 &&
                composeRule.onAllNodesWithText(actionText).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun navigateRepeatedly(destination: AppNavigationDestination) {
        composeRule.runOnIdle {
            val entry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateFrom(entry, destination) }
        }
        composeRule.waitForIdle()
    }

    private inline fun <reified T : AppNavigationDestination> popAndAssert() {
        composeRule.runOnIdle { assertTrue(navController.popBackStack()) }
        assertCurrentRoute<T>()
    }

    private inline fun <reified T : AppNavigationDestination> assertCurrentRoute() {
        composeRule.runOnIdle {
            assertTrue(navController.currentDestination?.hasRoute<T>() == true)
        }
    }
}
