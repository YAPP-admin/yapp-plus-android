package com.yapp.plus.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.auth.AuthSession
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: NavHostController
    private lateinit var restorationTester: StateRestorationTester
    private var authState by mutableStateOf<AppAuthUiState>(AppAuthUiState.Login())
    private var hasExited = false

    @Before
    fun setUp() {
        restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                AppStartContent(uiState = authState) { startDestination ->
                    navController = rememberNavController()
                    AppNavHost(
                        onExit = { hasExited = true },
                        onSignUpComplete = {
                            authState = AppAuthUiState.Authenticated(AuthSession("test-user"))
                        },
                        startDestination = startDestination,
                        navController = navController,
                    )
                }
            }
        }
    }

    @Test
    fun authenticatedSessionStartsMainWithoutLoginHistoryAndSurvivesRotation() {
        composeRule.runOnIdle {
            authState = AppAuthUiState.Authenticated(AuthSession("test-user"))
        }
        assertCurrentRoute<AppNavigationDestination.Main>()
        restorationTester.emulateSavedInstanceStateRestore()
        assertCurrentRoute<AppNavigationDestination.Main>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
        Espresso.pressBack()
        composeRule.runOnIdle { assertTrue(hasExited) }
    }

    @Test
    fun missingSessionCannotRestoreASavedMainDestination() {
        composeRule.runOnIdle {
            authState = AppAuthUiState.Authenticated(AuthSession("test-user"))
        }
        assertCurrentRoute<AppNavigationDestination.Main>()
        composeRule.runOnIdle { authState = AppAuthUiState.Restoring }
        composeRule.onNodeWithText("구글로 계속하기").assertDoesNotExist()
        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.runOnIdle { authState = AppAuthUiState.Login() }
        assertCurrentRoute<AppNavigationDestination.Login>()
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
    }

    @Test
    fun repeatedForwardCallbacksDoNotDuplicateDestinations() {
        assertCurrentRoute<AppNavigationDestination.Login>()
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.Main)

        popAndAssert<AppNavigationDestination.SignUp>()
        popAndAssert<AppNavigationDestination.Login>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
    }

    @Test
    fun repeatedBackCallbacksPopOnlyTheirOwnEntry() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.Main)

        composeRule.runOnIdle {
            val pendingEntry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateBackFrom(pendingEntry, onExit = {}) }
        }

        assertCurrentRoute<AppNavigationDestination.SignUp>()
    }

    @Test
    fun restoresTheDestinationAndCompleteBackStack() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        navigateRepeatedly(AppNavigationDestination.Main)

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AppNavigationDestination.Main>()
        popAndAssert<AppNavigationDestination.SignUp>()
        popAndAssert<AppNavigationDestination.Login>()
    }

    @Test
    fun signupRequiresProfileInputsBeforeCompleting() {
        composeRule.runOnIdle {
            authState = AppAuthUiState.SignUpRequired(AuthSession("test-user"))
        }
        assertCurrentRoute<AppNavigationDestination.SignUp>()
        composeRule.onNodeWithText("다음").assertIsNotEnabled()
        updateInput("이름")
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        composeRule.onNodeWithText("다음").assertIsNotEnabled()
        updateInput("임시 번호")
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        assertCurrentRoute<AppNavigationDestination.SignUp>()
        composeRule.onNodeWithText("회원가입을 완료했어요!").assertIsDisplayed()
        composeRule.onNodeWithText("운영진 승인을 기다리고 있어요...").assertDoesNotExist()
        composeRule.onNodeWithText("앱 둘러보기").assertDoesNotExist()
        composeRule.onNodeWithText("시작하기").performClick()
        assertCurrentRoute<AppNavigationDestination.Main>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
        Espresso.pressBack()
        composeRule.runOnIdle { assertTrue(hasExited) }
    }

    @Test
    fun preservesInputsAcrossRestorationAndNavigationBack() {
        navigateRepeatedly(AppNavigationDestination.SignUp)
        updateInput("이름")
        composeRule.onNodeWithText("다음").performClick()
        updateInput("01012345678")
        composeRule.onNodeWithText("다음").performClick()

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AppNavigationDestination.SignUp>()
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextContains("01012345678")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextContains("이름")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        navigateRepeatedly(AppNavigationDestination.SignUp)
        composeRule.onNode(hasSetTextAction()).assertTextContains("이름")
    }

    private fun updateInput(value: String) {
        val input = composeRule.onNode(hasSetTextAction())
        input.performTextInput(value)
        input.performImeAction()
        Espresso.closeSoftKeyboard()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == 1 &&
                composeRule.onAllNodesWithText("다음").fetchSemanticsNodes().isNotEmpty()
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
