package com.yapp.plus.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.board.BoardScreen
import com.yapp.plus.feature.main.home.HomeScreen
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.my.MyRoute
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UnconnectedFeaturesUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun boardShowsPreparingWithoutEmptyResultsOrRetry() {
        composeRule.setContent { YappTheme { BoardScreen() } }
        composeRule.onNodeWithText("게시판을 준비하고 있어요").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").assertDoesNotExist()
    }

    @Test
    fun myDoesNotExposeTestProfilesAndKeepsHistoryUnconnected() {
        composeRule.setContent { YappTheme { MyRoute(onSettingsClick = {}) } }
        composeRule.onNodeWithText("회원 정보 기능을 준비하고 있어요").assertIsDisplayed()
        composeRule.onNodeWithText("YAPP 테스트 프로필").assertDoesNotExist()
        composeRule.onNodeWithText("출석 내역").performClick()
        composeRule.onNodeWithText("출석 내역 기능을 준비하고 있어요").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").assertDoesNotExist()
    }

    @Test
    fun homeShowsNoticePreparationAndHidesUnconnectedEntries() {
        composeRule.setContent {
            YappTheme {
                HomeScreen(
                    state = AttendanceUiState.Empty,
                    noticeState = HomeNoticeUiState.Unavailable,
                )
            }
        }
        composeRule.onNodeWithText("공지를 준비하고 있어요").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("출석 점수").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("알림").assertDoesNotExist()
    }
}
