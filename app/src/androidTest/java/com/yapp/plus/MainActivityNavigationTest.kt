package com.yapp.plus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
    fun startsAtHomeAndShowsAllFourTabs() {
        assertSelectedTab("홈")
        composeRule.onNodeWithText("이번주 세션").assertIsDisplayed()
        selectTab("일정")
        composeRule.onNodeWithText("9.12 (토)").assertIsDisplayed()
        selectTab("게시판")
        composeRule.onNodeWithText("아직 작성된 공지사항이 없어요").assertIsDisplayed()
        selectTab("My")
        composeRule.onNodeWithText("마이페이지").assertIsDisplayed()
        selectTab("홈")
        composeRule.onNodeWithText("이번주 세션").assertIsDisplayed()
    }

    @Test
    fun exitsFromMainOnSystemBack() {
        val activity = composeRule.activity
        Espresso.pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(activity.isFinishing)
    }

    @Test
    fun preservesTheSelectedTabAcrossActivityRecreation() {
        selectTab("게시판")
        composeRule.activityRule.scenario.recreate()
        assertSelectedTab("게시판")
        composeRule.onNodeWithText("아직 작성된 공지사항이 없어요").assertIsDisplayed()
    }

    private fun selectTab(label: String) {
        composeRule.onNode(hasText(label) and hasClickAction()).performClick()
        assertSelectedTab(label)
    }

    private fun assertSelectedTab(label: String) {
        composeRule.onNode(hasText(label) and hasClickAction()).assertIsSelected()
    }
}
