package com.yapp.plus.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthDemoNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: NavHostController
    private lateinit var restorationTester: StateRestorationTester
    private var uiState by mutableStateOf(AuthDemoUiState())
    private var hasExited = false

    @Before
    fun setUp() {
        restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            navController = rememberNavController()
            YappTheme(darkTheme = false, dynamicColor = false) {
                AuthDemoNavHost(
                    uiState = uiState,
                    onNameChange = { uiState = uiState.copy(name = it) },
                    onPhoneNumberChange = { uiState = uiState.copy(phoneNumber = it) },
                    onExit = { hasExited = true },
                    startDestination = AuthDemoDestination.Login,
                    navController = navController,
                )
            }
        }
    }

    @Test
    fun repeatedForwardCallbacksDoNotDuplicateDestinations() {
        assertCurrentRoute<AuthDemoDestination.Login>()
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)
        navigateRepeatedly(AuthDemoDestination.Main)

        popAndAssert<AuthDemoDestination.Pending>()
        popAndAssert<AuthDemoDestination.Phone>()
        popAndAssert<AuthDemoDestination.Name>()
        popAndAssert<AuthDemoDestination.Login>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
    }

    @Test
    fun repeatedBackCallbacksPopOnlyTheirOwnEntry() {
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)

        composeRule.runOnIdle {
            val pendingEntry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateBackFrom(pendingEntry, onExit = {}) }
        }

        assertCurrentRoute<AuthDemoDestination.Phone>()
    }

    @Test
    fun restoresTheDestinationAndCompleteBackStack() {
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)
        navigateRepeatedly(AuthDemoDestination.Main)

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AuthDemoDestination.Main>()
        popAndAssert<AuthDemoDestination.Pending>()
        popAndAssert<AuthDemoDestination.Phone>()
        popAndAssert<AuthDemoDestination.Name>()
        popAndAssert<AuthDemoDestination.Login>()
    }

    @Test
    fun pendingActionOpensMainAndBackReturnsThroughTheAuthDemo() {
        composeRule.onNodeWithText("카카오톡으로 계속하기").performClick()
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        composeRule.onNodeWithText("앱 둘러보기").performClick()
        assertCurrentRoute<AuthDemoDestination.Main>()
        composeRule.onNodeWithText("이번주 세션").assertIsDisplayed()

        Espresso.pressBack()
        composeRule.onNodeWithText("운영진 승인을 기다리고 있어요...").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onAllNodesWithText("휴대폰 번호를 입력해 주세요")[0].assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithText("이름을 입력해 주세요").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithText("카카오톡으로 계속하기").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.runOnIdle { assertTrue(hasExited) }
    }

    @Test
    fun preservesInputsAcrossRestorationAndNavigationBack() {
        composeRule.onNodeWithText("카카오톡으로 계속하기").performClick()
        updateInput("이름")
        composeRule.onNodeWithText("다음").performClick()
        updateInput("01012345678")
        composeRule.onNodeWithText("다음").performClick()
        composeRule.onNodeWithText("앱 둘러보기").performClick()

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AuthDemoDestination.Main>()
        Espresso.pressBack()
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextContains("01012345678")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNode(hasSetTextAction()).assertTextContains("이름")
        composeRule.onNodeWithContentDescription("뒤로 가기").performClick()
        composeRule.onNodeWithText("카카오톡으로 계속하기").performClick()
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

    private fun navigateRepeatedly(destination: AuthDemoDestination) {
        composeRule.runOnIdle {
            val entry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateFrom(entry, destination) }
        }
        composeRule.waitForIdle()
    }

    private inline fun <reified T : AuthDemoDestination> popAndAssert() {
        composeRule.runOnIdle { assertTrue(navController.popBackStack()) }
        assertCurrentRoute<T>()
    }

    private inline fun <reified T : AuthDemoDestination> assertCurrentRoute() {
        composeRule.runOnIdle {
            assertTrue(navController.currentDestination?.hasRoute<T>() == true)
        }
    }
}
