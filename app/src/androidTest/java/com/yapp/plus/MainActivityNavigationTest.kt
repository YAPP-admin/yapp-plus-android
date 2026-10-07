package com.yapp.plus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun followsTheDemoWithoutValidationAndExitsFromLogin() {
        composeRule.onNodeWithText("카카오톡으로 계속하기").performClick()
        composeRule.onNodeWithText("이름을 입력해 주세요").assertIsDisplayed()
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        composeRule.onNodeWithText("운영진 승인을 기다리고 있어요...").assertIsDisplayed()

        Espresso.pressBack()
        composeRule.onAllNodesWithText("휴대폰 번호를 입력해 주세요")[0].assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithText("이름을 입력해 주세요").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithText("카카오톡으로 계속하기").assertIsDisplayed()

        val activity = composeRule.activity
        Espresso.pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(activity.isFinishing)
    }

    @Test
    fun preservesInputsAcrossActivityRecreationAndNavigationBack() {
        composeRule.onNodeWithText("카카오톡으로 계속하기").performClick()
        updateInput("이름")
        composeRule.onNodeWithText("다음").performClick()
        updateInput("01012345678")
        composeRule.onNodeWithText("다음").performClick()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText("운영진 승인을 기다리고 있어요...").assertIsDisplayed()
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
}
