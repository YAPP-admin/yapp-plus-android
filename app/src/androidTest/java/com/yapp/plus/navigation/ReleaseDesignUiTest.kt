package com.yapp.plus.navigation

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.login.LoginContent
import com.yapp.plus.feature.main.board.BoardScreen
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState
import com.yapp.plus.feature.main.home.AvailableHomePreview
import com.yapp.plus.feature.main.home.HomeAttendanceCompleteScreen
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import com.yapp.plus.feature.main.schedule.ScheduleScreen
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleTypeUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import com.yapp.plus.feature.setting.SettingsScreen
import com.yapp.plus.feature.signup.AuthStatusContent
import com.yapp.plus.feature.signup.SignUpContent
import com.yapp.plus.feature.signup.model.AuthStatus
import com.yapp.plus.feature.signup.model.SignUpStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ReleaseDesignUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureReleaseScreensAtReferenceSize() {
        var screen by mutableStateOf("login")
        val screens =
            listOf(
                "login",
                "name",
                "phone",
                "success",
                "pending",
                "problem",
                "home",
                "schedule",
                "board",
                "board-empty",
                "detail",
                "my",
                "settings",
                "done",
            )
        composeRule.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.requiredSize(393.dp, 756.dp).background(Color.White).testTag("screen")) {
                    CompositionLocalProvider(LocalInspectionMode provides true) {
                        key(screen) { ReferenceScreen(screen) }
                    }
                }
            }
        }
        for (name in screens) {
            composeRule.runOnIdle { screen = name }
            composeRule.waitForIdle()
            saveScreenshot(name)
        }
    }

    @Test
    fun categoryLabelsStayOnOneLineAndSelectionStillWorks() {
        var category by mutableStateOf(BoardCategory.All)
        composeRule.setContent {
            YappTheme {
                BoardScreen(category = category, onCategorySelected = { category = it })
            }
        }
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        composeRule.onNodeWithText("전체").performSemanticsAction(
            SemanticsActions.GetTextLayoutResult,
        ) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        composeRule.onNodeWithText("세션").performClick()
        composeRule.runOnIdle { assertEquals(BoardCategory.Session, category) }
    }

    @Test
    fun signUpActionHasReferenceHeight() {
        composeRule.setContent {
            YappTheme {
                SignUpContent(SignUpStep.Name, "김야뿌", {}, true, {}, {})
            }
        }
        composeRule.onNodeWithText("다음").assertIsDisplayed().assertHeightIsEqualTo(52.dp)
    }

    @Test
    fun settingsActionsRemainReachableOnCompactScreen() {
        var didLogout = false
        composeRule.setContent {
            YappTheme {
                Box(Modifier.requiredSize(320.dp, 480.dp)) {
                    SettingsScreen(
                        onBack = {},
                        onLogout = { didLogout = true },
                    )
                }
            }
        }
        composeRule.onNodeWithText("로그아웃").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("로그아웃 할까요?").assertIsDisplayed()
        composeRule.onAllNodesWithText("로그아웃").onLast().performClick()
        composeRule.runOnIdle { assertTrue(didLogout) }
    }

    private fun saveScreenshot(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val folder = File(context.getExternalFilesDir(null), "design-reference").apply { mkdirs() }
        File(folder, "$name.png").outputStream().use {
            composeRule
                .onNodeWithTag("screen")
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}

@Composable
private fun ReferenceScreen(screen: String) {
    when (screen) {
        "login" -> LoginContent({}, {})
        "name" -> SignUpContent(SignUpStep.Name, "", {}, false, {}, {})
        "phone" -> SignUpContent(SignUpStep.Phone, "01012345678", {}, true, {}, {})
        "success" -> AuthStatusContent(AuthStatus.Complete, {}, {})
        "pending" -> AuthStatusContent(AuthStatus.Pending, {}, {})
        "problem" -> AuthStatusContent(AuthStatus.Problem, {}, {})
        "home" -> ReferenceTab(MainTab.Home) { AvailableHomePreview() }
        "schedule" ->
            ReferenceTab(MainTab.Schedule) {
                ScheduleScreen(ScheduleUiState.Content(referenceSessions()))
            }
        "board" ->
            ReferenceTab(MainTab.Board) {
                BoardScreen(notices = NoticeUiState.Content(referenceNotices()))
            }
        "board-empty" -> ReferenceTab(MainTab.Board) { BoardScreen() }
        "detail" -> BoardScreen(detail = NoticeUiState.Content(referenceNotices().first()))
        "my" ->
            ReferenceTab(MainTab.My) {
                MyScreen(
                    MyUiState(
                        profile =
                            MyProfileUiState.Content(
                                MyProfileUiModel("김야뿌", "01012345678"),
                            ),
                    ),
                )
            }
        "settings" ->
            SettingsScreen(
                onBack = {},
                onLogout = {},
            )
        "done" -> HomeAttendanceCompleteScreen({})
    }
}

@Composable
private fun ReferenceTab(
    tab: MainTab,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) { content() }
        MainTabNavigation(tab, {})
    }
}

private fun referenceNotices() =
    listOf(
        BoardNoticeUiModel(
            1,
            "3주차 팀빌딩 세션 공지",
            "안녕하세요, 야뿌 여러분!\n프로젝트 진행을 위한 팀빌딩 과정의 첫 번째 단계인 '팀빌딩 자기소개서' 제출 안내드립니다.\n\n팀원들 간의 원활한 협업을 위해 아래 양식에 맞춰 작성해 주시기 바랍니다.\n\n문의 사항이 있으신 분은 운영진에게 연락 주세요.\n감사합니다.",
            BoardCategory.Session,
            "2026.09.30 (수)",
        ),
        BoardNoticeUiModel(2, "뒤풀이 참석 여부 수요 조사", "", BoardCategory.Operation, "2026.09.30 (수)"),
    )

private fun referenceSessions() =
    (0..5).map { index ->
        ScheduleSessionUiModel(
            dateLabel =
                java.time.LocalDate.of(2026, 9, 12).plusWeeks(index.toLong()).format(
                    java.time.format.DateTimeFormatter
                        .ofPattern("M.d (E)", java.util.Locale.KOREAN),
                ),
            name = if (index % 2 == 0) "팀 세션" else "기획 세션",
            location = if (index % 2 == 0) null else "ICT콤플렉스 6층 커뮤니티라운지",
            timeLabel = "오후 1시 ~ 오후 6시",
            type = if (index % 2 == 0) ScheduleTypeUiModel.Team else ScheduleTypeUiModel.Regular,
            status = if (index == 0) ScheduleStatusUiModel.Past else ScheduleStatusUiModel.Upcoming,
        )
    }
