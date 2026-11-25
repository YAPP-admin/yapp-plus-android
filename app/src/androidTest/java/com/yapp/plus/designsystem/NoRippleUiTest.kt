package com.yapp.plus.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoRippleUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun designSystemButtonKeepsItsAppearanceWhilePressedAndStillClicks() {
        var clicks = 0
        composeRule.setContent {
            YappTheme(dynamicColor = false) {
                Column {
                    YappButton("Enabled", { clicks++ }, modifier = Modifier.testTag("enabled"))
                    YappButton("Disabled", { clicks++ }, modifier = Modifier.testTag("disabled"), enabled = false)
                }
            }
        }

        pressAndCheckImage(composeRule.onNodeWithTag("enabled"))
        composeRule.onNodeWithTag("disabled").assertIsNotEnabled().performTouchInput { click() }
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun clickableModifierKeepsItsAppearanceWhilePressedAndStillClicks() {
        var clicks = 0
        composeRule.setContent {
            YappTheme(dynamicColor = false) {
                Box(
                    Modifier
                        .size(64.dp)
                        .background(Color.White)
                        .testTag("action")
                        .noRippleClickable(role = Role.Button) { clicks++ },
                )
            }
        }

        pressAndCheckImage(composeRule.onNodeWithTag("action"))
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    private fun pressAndCheckImage(node: SemanticsNodeInteraction) {
        val idleImage = node.captureToImage().asAndroidBitmap()
        node.performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        val pressedImage = node.captureToImage().asAndroidBitmap()
        node.performTouchInput { up() }
        assertTrue("The button changed visually while held", idleImage.sameAs(pressedImage))
    }
}
