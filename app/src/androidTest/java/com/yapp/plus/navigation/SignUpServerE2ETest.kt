package com.yapp.plus.navigation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.MainActivity
import com.yapp.plus.di.AuthE2ETestEntryPoint
import com.yapp.plus.domain.signup.PhoneNumber
import dagger.hilt.android.EntryPointAccessors
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.content.TextContent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.util.concurrent.ConcurrentLinkedQueue

@RunWith(AndroidJUnit4::class)
class SignUpServerE2ETest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: TestRule =
        RuleChain
            .outerRule(
                object : ExternalResource() {
                    override fun before() {
                        val arguments = InstrumentationRegistry.getArguments()
                        assumeTrue(arguments.getString("runLiveSignup") == "true")
                        if (arguments.getString("resetLocalSession") == "true") {
                            val context = InstrumentationRegistry.getInstrumentation().targetContext
                            val entryPoint =
                                EntryPointAccessors.fromApplication(
                                    context,
                                    AuthE2ETestEntryPoint::class.java,
                                )
                            runBlocking { entryPoint.tokenStore().updateTokens(null) }
                        }
                    }
                },
            ).around(composeRule)

    @Test
    fun nameErrorPreservesPhoneThroughLiveDevRegistration() {
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString("runLiveSignup") == "true")
        val name = requireNotNull(arguments.getString("signupName"))
        val phoneNumber = requireNotNull(arguments.getString("signupPhoneNumber"))
        val expectedPhone = requireNotNull(PhoneNumber(phoneNumber).toE164OrNull())
        val requests = ConcurrentLinkedQueue<Pair<String, String>>()
        val statuses = ConcurrentLinkedQueue<Int>()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val entryPoint =
            EntryPointAccessors.fromApplication(context, AuthE2ETestEntryPoint::class.java)
        entryPoint.unauthenticatedClient().plugin(HttpSend).intercept { request ->
            if (request.url.build().encodedPath == "/api/users") {
                val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                requests +=
                    body.getValue("name").jsonPrimitive.content to
                    body.getValue("phoneNumber").jsonPrimitive.content
                val response = execute(request)
                statuses += response.response.status.value
                Log.i(TAG, "registration_response=${response.response.status.value}")
                response
            } else {
                execute(request)
            }
        }

        waitForText("구글로 계속하기")
        composeRule.onNodeWithText("구글로 계속하기").performClick()
        Log.i(TAG, "phase=waiting_for_google_account")
        waitForText("이름을 입력해 주세요", GOOGLE_TIMEOUT_MS)
        Log.i(TAG, "phase=signup_started")
        replaceInput(INVALID_NAME)
        composeRule.onNodeWithText("다음").assertIsEnabled().performClick()
        waitForText("휴대폰 번호를 입력해 주세요")
        replaceInput(phoneNumber)
        composeRule.onNodeWithText("회원가입").assertIsEnabled().performClick()

        waitForText("이름은 100자 이하로 입력해 주세요", SERVER_TIMEOUT_MS)
        composeRule.onNodeWithText("이름을 입력해 주세요").assertIsDisplayed()
        composeRule.onNodeWithText("휴대폰 번호를 입력해 주세요").assertDoesNotExist()
        composeRule.onNode(hasSetTextAction()).assertTextEquals(INVALID_NAME)
        composeRule.onNodeWithText("회원가입").assertIsNotEnabled()
        assertEquals(listOf(400), statuses.toList())
        capture("signup-name-error.png")
        Log.i(TAG, "phase=name_error_returned_without_phone_reentry")

        replaceInput(name)
        composeRule.onNodeWithText("휴대폰 번호를 입력해 주세요").assertDoesNotExist()
        composeRule.onNodeWithText("회원가입").assertIsEnabled().performClick()
        composeRule.waitUntil(timeoutMillis = SERVER_TIMEOUT_MS) { statuses.size == 2 }
        assertEquals(listOf(INVALID_NAME to expectedPhone, name to expectedPhone), requests.toList())
        Log.i(TAG, "phase=resubmission_phone_preserved")
        assertEquals(listOf(400, 200), statuses.toList())
        waitForText("회원가입을 완료했어요!", SERVER_TIMEOUT_MS)
        runBlocking { assertNotNull(entryPoint.tokenStore().currentTokens()) }
        capture("signup-complete.png")
        Log.i(TAG, "phase=signup_completed_phone_preserved")

        composeRule.onNodeWithText("시작하기").performClick()
        waitForText("홈")
        composeRule.onNodeWithText("게시판").assertIsDisplayed()
        composeRule.onNodeWithText("구글로 계속하기").assertDoesNotExist()
        capture("signup-main.png")
        composeRule.activityRule.scenario.recreate()
        waitForText("홈")
        composeRule.onNodeWithText("구글로 계속하기").assertDoesNotExist()
        Log.i(TAG, "phase=main_and_activity_restoration_verified")
    }

    private fun replaceInput(value: String) {
        composeRule
            .onNode(hasSetTextAction())
            .performClick()
            .assertIsFocused()
            .performTextReplacement(value)
        composeRule.onNode(hasSetTextAction()).performImeAction()
        Espresso.closeSoftKeyboard()
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis = UI_TIMEOUT_MS) {
            composeRule.onAllNodesWithText("다음").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithText("회원가입").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForText(
        text: String,
        timeoutMs: Long = UI_TIMEOUT_MS,
    ) {
        composeRule.waitUntil(timeoutMillis = timeoutMs) {
            try {
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            } catch (exception: IllegalStateException) {
                if (exception.message?.startsWith("No compose hierarchies") == true) {
                    false
                } else {
                    throw exception
                }
            }
        }
        composeRule.onAllNodesWithText(text).onFirst().assertIsDisplayed()
    }

    private fun capture(filename: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = context.cacheDir.resolve("signup-e2e").apply { mkdirs() }
        directory.resolve(filename).outputStream().use { output ->
            composeRule
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    private companion object {
        const val TAG = "YappSignupE2E"
        const val UI_TIMEOUT_MS = 15_000L
        const val SERVER_TIMEOUT_MS = 90_000L
        const val GOOGLE_TIMEOUT_MS = 180_000L
        val INVALID_NAME = "A".repeat(101)
    }
}
