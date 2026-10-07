package com.yapp.plus.navigation

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.theme.YappTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthDemoNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: NavHostController
    private lateinit var restorationTester: StateRestorationTester

    @Before
    fun setUp() {
        restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            navController = rememberNavController()
            YappTheme(darkTheme = false, dynamicColor = false) {
                AuthDemoNavHost(
                    uiState = AuthDemoUiState(),
                    onNameChange = {},
                    onPhoneNumberChange = {},
                    onExit = {},
                    navController = navController,
                )
            }
        }
    }

    @Test
    fun repeatedForwardCallbacksDoNotDuplicateDestinations() {
        assertCurrentRoute<AuthDemoDestination.Login>()
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)

        popAndAssert<AuthDemoDestination.Phone>()
        popAndAssert<AuthDemoDestination.Name>()
        popAndAssert<AuthDemoDestination.Login>()
        composeRule.runOnIdle { assertTrue(navController.previousBackStackEntry == null) }
    }

    @Test
    fun repeatedBackCallbacksPopOnlyTheirOwnEntry() {
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)

        composeRule.runOnIdle {
            val pendingEntry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateBackFrom(pendingEntry, onExit = {}) }
        }

        assertCurrentRoute<AuthDemoDestination.Phone>()
    }

    @Test
    fun restoresTheDestinationAndCompleteBackStack() {
        navigateRepeatedly(AuthDemoDestination.Name)
        navigateRepeatedly(AuthDemoDestination.Phone)
        navigateRepeatedly(AuthDemoDestination.Pending)

        restorationTester.emulateSavedInstanceStateRestore()

        assertCurrentRoute<AuthDemoDestination.Pending>()
        popAndAssert<AuthDemoDestination.Phone>()
        popAndAssert<AuthDemoDestination.Name>()
        popAndAssert<AuthDemoDestination.Login>()
    }

    private fun navigateRepeatedly(destination: AuthDemoDestination) {
        composeRule.runOnIdle {
            val entry = checkNotNull(navController.currentBackStackEntry)
            repeat(5) { navController.navigateFrom(entry, destination) }
        }
        composeRule.waitForIdle()
    }

    private inline fun <reified T : AuthDemoDestination> popAndAssert() {
        composeRule.runOnIdle { assertTrue(navController.popBackStack()) }
        assertCurrentRoute<T>()
    }

    private inline fun <reified T : AuthDemoDestination> assertCurrentRoute() {
        composeRule.runOnIdle {
            assertTrue(navController.currentDestination?.hasRoute<T>() == true)
        }
    }
}
