package com.yapp.plus.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.HomeScreen
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.my.MyAttendanceHistoryScreen
import com.yapp.plus.feature.main.my.MyPreviewData
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import org.junit.Rule
import org.junit.Test

class InitialLoadingUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val progress = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)

    @Test
    fun homeShowsOneIndicatorWhileBothSectionsAreLoading() {
        var isLoading by mutableStateOf(true)
        composeRule.setContent {
            YappTheme {
                HomeScreen(
                    state = if (isLoading) AttendanceUiState.Loading else AttendanceUiState.Empty,
                    noticeState = if (isLoading) HomeNoticeUiState.Loading else HomeNoticeUiState.Empty,
                    isLoading = isLoading,
                )
            }
        }
        composeRule.onAllNodes(progress).assertCountEquals(1)
        composeRule.runOnIdle { isLoading = false }
        composeRule.onAllNodes(progress).assertCountEquals(0)
        composeRule.onNodeWithText("이번 주 예정된 세션이 없어요").assertIsDisplayed()
    }

    @Test
    fun myKeepsSavedNameVisibleBesideOneIndicator() {
        composeRule.setContent {
            YappTheme {
                MyScreen(
                    state =
                        MyUiState(
                            profile = MyProfileUiState.Loading("저장된 이름"),
                            isLoading = true,
                        ),
                )
            }
        }
        composeRule.onAllNodes(progress).assertCountEquals(1)
        composeRule.onNodeWithText("저장된 이름").assertIsDisplayed()
    }

    @Test
    fun historyShowsOneIndicatorUntilTheInitialRequestsHaveFinished() {
        var isLoading by mutableStateOf(true)
        composeRule.setContent {
            YappTheme {
                MyAttendanceHistoryScreen(
                    state = MyPreviewData.history,
                    onBack = {},
                    onRetry = {},
                    onLoadMore = {},
                    isScreenLoading = isLoading,
                )
            }
        }
        composeRule.onAllNodes(progress).assertCountEquals(1)
        composeRule.onNodeWithText("세션 출석 내역").assertDoesNotExist()
        composeRule.runOnIdle { isLoading = false }
        composeRule.onAllNodes(progress).assertCountEquals(0)
        composeRule.onNodeWithText("세션 출석 내역").assertIsDisplayed()
    }
}
