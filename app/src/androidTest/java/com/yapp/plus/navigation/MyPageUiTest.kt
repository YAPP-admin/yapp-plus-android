package com.yapp.plus.navigation

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.MyAttendanceHistoryScreen
import com.yapp.plus.feature.main.my.MyPreviewData
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MySessionSummary
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class MyPageUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun historyNavigationAndRulesWorkWithoutInlineExpansion() {
        var history by mutableStateOf(false)
        var myState by mutableStateOf(MyPreviewData.state)
        composeRule.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                Box(
                    Modifier
                        .requiredSize(
                            393.dp,
                            756.dp,
                        ).background(Color.White)
                        .testTag("my-screen"),
                ) {
                    if (history) {
                        MyAttendanceHistoryScreen(
                            state = MyPreviewData.history,
                            onBack = { history = false },
                            onRetry = {},
                            onLoadMore = {},
                        )
                    } else {
                        MyScreen(
                            state = myState,
                            onAttendanceHistoryClick = { history = true },
                            onContactClick = {},
                        )
                    }
                }
            }
        }
        saveScreenshot("my-summary")
        composeRule.onNodeWithContentDescription("출석 규칙").performClick()
        composeRule.onNodeWithText("1시간 지각/조퇴").assertIsDisplayed()
        composeRule.onNodeWithText("-20점").assertIsDisplayed()
        saveWindowScreenshot("my-rules")
        composeRule.onNodeWithText("확인").performClick()
        composeRule.onNodeWithText("출석 내역").performClick()
        composeRule.onNodeWithText("세션 출석 내역").assertIsDisplayed()
        composeRule.onNodeWithText("개발-디자인 세션").assertIsDisplayed()
        composeRule.onNodeWithText("마이페이지").assertDoesNotExist()
        saveScreenshot("my-history")
        composeRule.onNodeWithContentDescription("마이페이지로 돌아가기").performClick()
        composeRule.onNodeWithText("내 출석 현황").assertIsDisplayed()
        composeRule.runOnIdle {
            myState =
                MyPreviewData.state.copy(
                    profile = MyProfileUiState.Unavailable,
                    score = MyPreviewData.score.copy(lateExemptionCount = null),
                )
        }
        composeRule.onNodeWithText("김야뿌").assertDoesNotExist()
        composeRule.onNodeWithText("지각 면제권").assertIsDisplayed()
        composeRule.onNodeWithText("이용문의").assertIsDisplayed()
        saveScreenshot("my-unconnected")
    }

    @Test
    fun emptyHistoryShowsFigmaGraphicAndServerCounts() {
        composeRule.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                Box(
                    Modifier
                        .requiredSize(
                            393.dp,
                            756.dp,
                        ).background(Color.White)
                        .testTag("my-screen"),
                ) {
                    MyAttendanceHistoryScreen(
                        state =
                            MyAttendanceHistoryState.Content(
                                MySessionSummary(12, 12, 0),
                                emptyList(),
                            ),
                        onBack = {},
                        onRetry = {},
                        onLoadMore = {},
                    )
                }
            }
        }
        composeRule.onNodeWithText("아직 출석 내역이 없어요").assertIsDisplayed()
        composeRule.onNodeWithText("진행률").assertIsDisplayed()
        saveScreenshot("my-history-empty")
    }

    @Test
    fun loadingFailureRetriesWithoutInventingAttendance() {
        var retries = 0
        composeRule.setContent {
            YappTheme {
                MyAttendanceHistoryScreen(
                    state = MyAttendanceHistoryState.Error,
                    onBack = {},
                    onRetry = { retries++ },
                    onLoadMore = {},
                )
            }
        }
        composeRule.onNodeWithText("아직 출석 내역이 없어요").assertDoesNotExist()
        composeRule.onNodeWithText("다시 시도").performClick()
        composeRule.runOnIdle { assertEquals(1, retries) }
    }

    private fun saveScreenshot(name: String) {
        val bitmap = composeRule.onNodeWithTag("my-screen").captureToImage().asAndroidBitmap()
        saveBitmap(bitmap, name)
    }

    private fun saveWindowScreenshot(name: String) {
        val bitmap =
            checkNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
        saveBitmap(bitmap, name)
    }

    private fun saveBitmap(
        bitmap: Bitmap,
        name: String,
    ) {
        val directory =
            File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "my-figma")
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
    }
}
