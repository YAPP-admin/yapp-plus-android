package com.yapp.plus.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.board.BoardScreen
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState
import com.yapp.plus.feature.main.home.components.HomeReportReasonPreview
import com.yapp.plus.feature.main.home.components.HomeReportTypePreview
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class ConnectedApisUiTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun boardOpensServerDetailAndRequiresReadConfirmation() {
        val notice =
            BoardNoticeUiModel(
                7,
                "세션 안내",
                "**중요한 공지**\n\n이번 주 세션 안내입니다.",
                BoardCategory.Session,
                "2026.10.30 (금)",
                readCount = 12,
            )
        var detail by mutableStateOf<NoticeUiState<BoardNoticeUiModel>?>(null)
        var confirmations = 0
        composeRule.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                BoardScreen(
                    notices = NoticeUiState.Content(listOf(notice)),
                    detail = detail,
                    onNoticeClick = { detail = NoticeUiState.Content(notice) },
                    onMarkRead = {
                        confirmations++
                        detail =
                            NoticeUiState.Content(notice.copy(isRead = true, readCount = 13))
                    },
                )
            }
        }
        composeRule.onNodeWithText("세션 안내").performClick()
        composeRule.runOnIdle { assertEquals(0, confirmations) }
        composeRule.onNodeWithText("확인하기").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals(1, confirmations) }
        composeRule.onNodeWithText("확인했어요").assertIsDisplayed()
        screenshot("notice-detail")
    }

    @Test fun reportTypeMatchesFigmaOptions() {
        composeRule.setContent { HomeReportTypePreview() }
        composeRule.onNodeWithText("지각").assertIsDisplayed()
        composeRule.onNodeWithText("조퇴").assertIsDisplayed()
        composeRule.onNodeWithText("결석").assertIsDisplayed()
        screenshot("report-type")
    }

    @Test fun reportReasonShowsServerLengthLimit() {
        composeRule.setContent { HomeReportReasonPreview() }
        composeRule.onNodeWithText("조퇴 사유를 입력해 주세요").assertIsDisplayed()
        composeRule.onNodeWithText("5/30").assertIsDisplayed()
        screenshot("report-reason")
    }

    @Test fun myDisplaysSavedNameWithoutInventingMembership() {
        composeRule.setContent {
            YappTheme {
                MyScreen(
                    state =
                        MyUiState(
                            profile = MyProfileUiState.Content(MyProfileUiModel("서버 이름")),
                        ),
                )
            }
        }
        composeRule.onNodeWithText("서버 이름").assertIsDisplayed()
        composeRule.onNodeWithText("활동회원").assertDoesNotExist()
        composeRule.onNodeWithText("28기").assertDoesNotExist()
        screenshot("my-name")
    }

    private fun screenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = composeRule.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(instrumentation.targetContext.cacheDir, "api-161").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
