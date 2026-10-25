package com.yapp.plus.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.setting.SettingsScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        composeRule.onNodeWithText("앱 계정 삭제").assertIsNotEnabled()
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
                )
            }
        }
        composeRule.onNodeWithText("yappplus.official@gmail.com").assertIsDisplayed()
        composeRule.onNodeWithText("앱 계정 삭제").performClick()
        restoration.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("앱 계정을 삭제할까요?").assertIsDisplayed()
        composeRule.onNodeWithText("취소").performClick()
        composeRule.runOnIdle { assertEquals(0, requests) }
        composeRule.onNodeWithText("앱 계정 삭제").performClick()
        composeRule.onNodeWithText("계정 삭제").performClick()
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
        composeRule.onNodeWithText("앱 계정 삭제").performClick()
        composeRule.onNodeWithText("계정 삭제").performClick()
        composeRule.onNodeWithText("계정을 삭제하고 있어요").assertIsDisplayed()
        composeRule.onNodeWithText("계정 삭제").assertDoesNotExist()
        composeRule.onNodeWithText("로그아웃").assertIsNotEnabled()
        Espresso.pressBack()
        composeRule.onNodeWithText("계정을 삭제하고 있어요").assertIsDisplayed()
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
        composeRule.onNodeWithText("계정 삭제 실패").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").performClick()
        composeRule.runOnIdle {
            assertEquals(1, retries)
            hasError = true
        }
        composeRule.onNodeWithText("취소").performClick()
        composeRule.onNodeWithText("계정 삭제 실패").assertDoesNotExist()
    }
}
