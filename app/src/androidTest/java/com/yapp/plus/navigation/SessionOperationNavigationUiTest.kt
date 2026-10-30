package com.yapp.plus.navigation

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.testing.NavigationTestActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionOperationNavigationUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<NavigationTestActivity>()

    @Test
    fun logoutSuccessClearsMainBackStackAndShowsLogin() {
        assertSessionOperationReturnsToLogin(isLogout = true)
    }

    @Test
    fun withdrawalSuccessClearsMainBackStackAndShowsLogin() {
        assertSessionOperationReturnsToLogin(isLogout = false)
    }

    private fun assertSessionOperationReturnsToLogin(isLogout: Boolean) {
        var hasSession by mutableStateOf(true)
        var logoutRequests = 0
        var withdrawalRequests = 0
        lateinit var navController: NavHostController
        composeRule.setContent {
            YappTheme(darkTheme = false, dynamicColor = false) {
                navController = rememberNavController()
                AppNavHost(
                    onExit = {},
                    onLogout = {
                        logoutRequests++
                        hasSession = false
                    },
                    onDeleteAccount = {
                        withdrawalRequests++
                        hasSession = false
                    },
                    sessionAuthenticated = hasSession,
                    startDestination = AppNavigationDestination.Main,
                    navController = navController,
                    modifier = Modifier.safeDrawingPadding(),
                )
            }
        }
        composeRule.onNodeWithText("MY").performClick()
        composeRule.onNodeWithContentDescription("설정").performClick()
        if (isLogout) {
            confirmLogout()
        } else {
            composeRule.onNodeWithText("회원 탈퇴").performClick()
            composeRule.onNodeWithText("탈퇴하기").performClick()
        }
        composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
        composeRule.runOnIdle {
            val destination = navController.currentDestination
            assertTrue(destination?.hasRoute<AppNavigationDestination.Login>() == true)
            assertNull(navController.previousBackStackEntry)
            assertEquals(if (isLogout) 1 else 0, logoutRequests)
            assertEquals(if (isLogout) 0 else 1, withdrawalRequests)
        }
    }

    private fun confirmLogout() {
        composeRule.onNodeWithText("로그아웃").performClick()
        composeRule
            .onNode(hasText("로그아웃") and hasAnyAncestor(isDialog()))
            .performClick()
    }
}
