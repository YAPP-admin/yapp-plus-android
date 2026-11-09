package com.yapp.plus.notification

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.setting.NotificationSettingsUiState
import com.yapp.plus.feature.setting.SettingsScreen
import com.yapp.plus.feature.setting.components.SettingsNotificationSection
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class NotificationSettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun toggleKeepsConfirmedValueUntilUpdatedStateArrives() {
        var state by mutableStateOf(NotificationSettingsUiState(isEnabled = true))
        var requests = 0
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    onBack = {},
                    onLogout = {},
                    notificationSettings = {
                        SettingsNotificationSection(
                            state = state,
                            onEnabledChange = {
                                requests++
                                state = state.copy(isUpdating = true)
                            },
                            onRetry = {},
                            onOpenSettings = {},
                        )
                    },
                )
            }
        }
        composeRule.onNodeWithText("알림").assertIsOn().performClick()
        composeRule.onNodeWithText("알림").assertIsOn().assertIsNotEnabled()
        composeRule.onNodeWithText("알림 설정을 변경하고 있어요").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(1, requests)
            state = state.copy(isUpdating = false, isEnabled = false)
        }
        composeRule.onNodeWithText("알림").assertIsOff()
        composeRule.onNodeWithText("이 기기에서 알림 받기").assertDoesNotExist()
        saveScreenshot("notification-off")
    }

    @Test
    fun loadFailureDisablesToggleAndOffersRetryAndSystemSettings() {
        var retries = 0
        var settings = 0
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    onBack = {},
                    onLogout = {},
                    notificationSettings = {
                        SettingsNotificationSection(
                            state =
                                NotificationSettingsUiState(
                                    hasError = true,
                                    isPermissionGranted = false,
                                ),
                            onEnabledChange = { error("Toggle is unavailable while loading") },
                            onRetry = { retries++ },
                            onOpenSettings = { settings++ },
                        )
                    },
                )
            }
        }
        saveScreenshot("notification-error")
        composeRule.onNodeWithText("알림").assertIsNotEnabled()
        composeRule.onNodeWithText("다시 시도").performClick()
        composeRule.onNodeWithText("휴대폰 설정에서 알림 허용하기").performClick()
        composeRule.runOnIdle {
            assertEquals(1, retries)
            assertEquals(1, settings)
        }
    }

    private fun saveScreenshot(name: String) {
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = composeRule.onRoot().captureToImage().asAndroidBitmap()
        File(context.cacheDir, "$name.png").outputStream().use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        }
        bitmap.recycle()
    }
}
