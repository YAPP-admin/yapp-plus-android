package com.yapp.plus.navigation

import android.graphics.Bitmap
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.setting.SettingsScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AccountDeletionUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun authenticationActionsAreDisabledWithoutServerCallbacks() {
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    appVersion = "1.0",
                    onBack = {},
                    onLogout = null,
                )
            }
        }
        composeRule.onNodeWithText("알림").assertDoesNotExist()
        composeRule.onNodeWithText("YAPP 탈퇴").assertIsNotEnabled()
        composeRule.onNodeWithText("로그아웃").assertIsNotEnabled()
    }

    @Test
    fun confirmationSurvivesRestorationAndCancelDoesNotDelete() {
        var requests = 0
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent {
            YappTheme {
                SettingsScreen(
                    appVersion = "1.0",
                    onBack = {},
                    onLogout = {},
                    onDeleteAccount = { requests++ },
                    modifier = Modifier.safeDrawingPadding(),
                )
            }
        }
        composeRule.onNodeWithText("yappplus.official@gmail.com").assertIsDisplayed()
        composeRule.onNodeWithText("YAPP 탈퇴").performClick()
        restoration.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("YAPP에서 탈퇴할까요?").assertIsDisplayed()
        composeRule
            .onNodeWithText(
                "모든 기수의 YAPP 회원 자격에서 탈퇴 처리되고 모든 세션이 종료돼요. " +
                    "이름·전화번호 등 개인정보는 비식별화되어 복구할 수 없어요.",
            ).assertIsDisplayed()
        saveConfirmationScreenshot()
        composeRule.onNodeWithText("취소").performClick()
        composeRule.runOnIdle { assertEquals(0, requests) }
        composeRule.onNodeWithText("YAPP 탈퇴").performClick()
        composeRule.onNodeWithText("탈퇴하기").performClick()
        composeRule.runOnIdle { assertEquals(1, requests) }
    }

    @Test
    fun pendingDeletionCannotBeDismissedOrRepeated() {
        var deleting by mutableStateOf(false)
        var requests = 0
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    appVersion = "1.0",
                    onBack = {},
                    onLogout = {},
                    onDeleteAccount = {
                        requests++
                        deleting = true
                    },
                    isDeletingAccount = deleting,
                )
            }
        }
        composeRule.onNodeWithText("YAPP 탈퇴").performClick()
        composeRule.onNodeWithText("탈퇴하기").performClick()
        composeRule.onNodeWithText("탈퇴 처리 중이에요").assertIsDisplayed()
        composeRule.onNodeWithText("탈퇴하기").assertDoesNotExist()
        composeRule.onNodeWithText("로그아웃").assertIsNotEnabled()
        Espresso.pressBack()
        composeRule.onNodeWithText("탈퇴 처리 중이에요").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(1, requests) }
    }

    @Test
    fun deletionFailureOffersRetryAndDismissal() {
        var hasError by mutableStateOf(true)
        var retries = 0
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    appVersion = "1.0",
                    onBack = {},
                    onLogout = {},
                    onDeleteAccount = {
                        retries++
                        hasError = false
                    },
                    hasDeleteAccountError = hasError,
                    onDeleteAccountErrorDismissed = { hasError = false },
                )
            }
        }
        composeRule.onNodeWithText("탈퇴 처리 실패").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").performClick()
        composeRule.runOnIdle {
            assertEquals(1, retries)
            hasError = true
        }
        composeRule.onNodeWithText("취소").performClick()
        composeRule.onNodeWithText("탈퇴 처리 실패").assertDoesNotExist()
    }

    private fun saveConfirmationScreenshot() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        val file = File(instrumentation.targetContext.cacheDir, "withdrawal-confirmation.png")
        file.outputStream().use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        }
        bitmap.recycle()
    }
}
