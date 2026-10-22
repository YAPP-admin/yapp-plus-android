package com.yapp.plus.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.data.auth.AuthRepositoryImpl
import com.yapp.plus.data.auth.SupabaseAuthDataSource
import com.yapp.plus.feature.login.LoginContent
import com.yapp.plus.feature.setting.SettingsScreen
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class AccountDeletionFlowTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun confirmationCallsRpcAndRetryClearsSessionBeforeLogin() {
        val tokens = DeletionTestTokenStore()
        val requests = AtomicInteger()
        val releaseFailure = CompletableDeferred<Unit>()
        val client =
            HttpClient(
                MockEngine { request ->
                    when (request.url.encodedPath) {
                        "/auth/v1/user" -> respond("""{"id":"test-user"}""")
                        "/rest/v1/rpc/mvp_delete_account" -> {
                            assertEquals(HttpMethod.Post, request.method)
                            assertTrue(request.url.parameters.isEmpty())
                            if (requests.incrementAndGet() == 1) {
                                releaseFailure.await()
                                respond("unavailable", HttpStatusCode.InternalServerError)
                            } else {
                                respond("""{"deleted":true,"user_id":"test-user"}""")
                            }
                        }
                        else -> error("Unexpected mocked request: ${request.url.encodedPath}")
                    }
                },
            )
        val repository =
            AuthRepositoryImpl(
                SupabaseAuthDataSource(
                    httpClient = client,
                    projectUrl = "https://example.invalid",
                    publishableKey = "sb_publishable_test",
                    tokenLocalDataSource = tokens,
                ),
            )
        val store = ViewModelStore()
        lateinit var viewModel: AppAuthViewModel
        var lastDestination: AppNavigationDestination? = null
        composeRule.runOnUiThread {
            viewModel = AppAuthViewModel(repository, SavedStateHandle())
            store.put("auth", viewModel)
        }
        try {
            composeRule.setContent {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                YappTheme {
                    AppStartContent(uiState = state) { destination ->
                        lastDestination = destination
                        val authenticated = state as? AppAuthUiState.Authenticated
                        if (authenticated != null) {
                            SettingsScreen(
                                appVersion = "1.0",
                                notificationsEnabled = true,
                                onNotificationEnabledChange = {},
                                onBack = {},
                                onLogout = viewModel::signOut,
                                onDeleteAccount = viewModel::deleteAccount,
                                isDeletingAccount = authenticated.isDeletingAccount,
                                hasDeleteAccountError = authenticated.hasDeleteAccountError,
                                onDeleteAccountErrorDismissed = viewModel::dismissDeleteAccountError,
                            )
                        } else {
                            LoginContent(onBack = {}, onGoogleLogin = {})
                        }
                    }
                }
            }
            composeRule.waitUntil(5_000) { viewModel.uiState.value is AppAuthUiState.Authenticated }
            composeRule.onNodeWithText("앱 계정 삭제").performClick()
            capture("confirmation")
            composeRule.onNodeWithText("계정 삭제").performClick()
            composeRule.waitUntil(5_000) { requests.get() == 1 }
            composeRule.onNodeWithText("계정을 삭제하고 있어요").assertIsDisplayed()
            assertEquals("fake-access", tokens.tokens.accessToken)
            capture("pending")
            releaseFailure.complete(Unit)
            composeRule.waitUntil(5_000) {
                (viewModel.uiState.value as? AppAuthUiState.Authenticated)?.hasDeleteAccountError == true
            }
            composeRule.onNodeWithText("계정 삭제 실패").assertIsDisplayed()
            assertEquals("fake-access", tokens.tokens.accessToken)
            capture("failure")
            composeRule.onNodeWithText("다시 시도").performClick()
            composeRule.waitUntil(5_000) { viewModel.uiState.value is AppAuthUiState.Login }
            composeRule.onNodeWithText("구글로 계속하기").assertIsDisplayed()
            assertEquals(2, requests.get())
            assertEquals(SessionTokenRecord(), tokens.tokens)
            composeRule.runOnIdle {
                assertEquals(AppNavigationDestination.Login, lastDestination)
            }
            composeRule.onNodeWithText("앱 계정 삭제").assertDoesNotExist()
            capture("login")
        } finally {
            releaseFailure.complete(Unit)
            composeRule.runOnUiThread { store.clear() }
            client.close()
        }
    }

    private fun capture(name: String) {
        composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        File(instrumentation.targetContext.cacheDir, "deletion-$name.png").outputStream().use {
            instrumentation.uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
