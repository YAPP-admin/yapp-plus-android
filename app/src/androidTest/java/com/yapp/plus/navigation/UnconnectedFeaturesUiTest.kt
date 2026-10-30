package com.yapp.plus.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.board.BoardScreen
import com.yapp.plus.feature.main.home.HomeScreen
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState
import com.yapp.plus.feature.main.my.model.MyUiState
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
    fun myKeepsFigmaUiWithoutInventingUnconnectedValues() {
        composeRule.setContent {
            YappTheme { MyScreen(state = MyUiState(score = MyAttendanceScoreState.Ineligible)) }
        }
        composeRule.onNodeWithText("김야뿌").assertDoesNotExist()
        composeRule.onNodeWithText("지각 면제권").assertIsDisplayed()
        composeRule.onNodeWithText("이용문의").assertIsDisplayed()
        composeRule.onNodeWithText("내 출석 현황").assertIsDisplayed()
        composeRule.onNodeWithText("0").assertDoesNotExist()
        composeRule.onNodeWithText("92점").assertDoesNotExist()
        composeRule.onNodeWithText("출석 내역").assertIsDisplayed()
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
