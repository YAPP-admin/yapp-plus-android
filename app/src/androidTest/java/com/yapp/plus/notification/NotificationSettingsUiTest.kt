package com.yapp.plus.notification

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.setting.NotificationSettingsUiState
import com.yapp.plus.feature.setting.SettingsScreen
import com.yapp.plus.feature.setting.components.SettingsNotificationSection
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationSettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun notificationToggleOpensSystemSettingsWithoutSeparateSettingsRow() {
        var requestedEnabled: Boolean? = null
        composeRule.setContent {
            YappTheme {
                SettingsScreen(
                    onBack = {},
                    onLogout = {},
                    notificationSettings = {
                        SettingsNotificationSection(
                            state =
                                NotificationSettingsUiState(
                                    isEnabled = false,
                                    isPermissionGranted = false,
                                ),
                            onEnabledChange = { requestedEnabled = it },
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithText("알림").assertIsOff().performClick()
        composeRule.runOnIdle { assertEquals(true, requestedEnabled) }
        composeRule.onAllNodes(isToggleable()).assertCountEquals(1)
        composeRule.onNodeWithText("휴대폰 설정에서 알림 허용하기").assertDoesNotExist()
    }

    @Test
    fun settingsUpdateErrorDoesNotShowRetryUi() {
        composeRule.setContent {
            YappTheme {
                SettingsNotificationSection(
                    state = NotificationSettingsUiState(isEnabled = true, hasUpdateError = true),
                    onEnabledChange = {},
                )
            }
        }

        composeRule.onNodeWithText("알림").assertIsOn()
        composeRule.onNodeWithText("설정을 변경하지 못했어요. 다시 시도해 주세요.").assertDoesNotExist()
        composeRule.onNodeWithText("다시 시도").assertDoesNotExist()
    }
}
