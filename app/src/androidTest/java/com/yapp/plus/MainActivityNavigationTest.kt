package com.yapp.plus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
    fun startsAtLoginWithoutAStoredSupabaseSession() {
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        composeRule.onNodeWithText("이번주 세션").assertDoesNotExist()
    }

    @Test
    fun exitsFromLoginOnSystemBack() {
        val activity = composeRule.activity
        Espresso.pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(activity.isFinishing)
    }

    @Test
    fun preservesLoginAcrossActivityRecreation() {
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
    }
}
