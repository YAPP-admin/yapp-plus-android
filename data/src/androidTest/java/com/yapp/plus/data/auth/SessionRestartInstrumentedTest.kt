package com.yapp.plus.data.auth

import android.content.Context
import android.os.Process
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.data.auth.datasource.session.SessionTokenLocalDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Each phase runs in a fresh process through scripts/verify_session_restart.py. */
@RunWith(AndroidJUnit4::class)
class SessionRestartInstrumentedTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val marker = File(context.noBackupFilesDir, "session-restart-test.marker")
    private val runId = InstrumentationRegistry.getArguments().getString("sessionTestRun")

    @Test
    fun persistRotatedPair() =
        runBlocking {
            requirePhase("persist")
            val store = createStore()
            val original = Tokens(accessToken = OLD_ACCESS, refreshToken = OLD_REFRESH)
            store.updateTokens(original)
            assertTrue(
                store.replaceTokens(
                    expected = original,
                    updated = Tokens(accessToken = NEW_ACCESS, refreshToken = NEW_REFRESH),
                ),
            )
            val files = File(context.noBackupFilesDir, "sessions").listFiles().orEmpty()
            assertTrue(files.isNotEmpty())
            for (file in files) {
                val content = file.readBytes().decodeToString()
                assertFalse(content.contains(NEW_ACCESS))
                assertFalse(content.contains(NEW_REFRESH))
            }
            saveProcessMarker()
        }

    @Test
    fun restoreAndClearPair() =
        runBlocking {
            requirePhase("restore")
            assertDifferentProcess()
            val store = createStore()
            val restored = store.currentTokens()
            assertEquals(NEW_ACCESS, restored?.accessToken)
            assertEquals(NEW_REFRESH, restored?.refreshToken)
            store.updateTokens(null)
            assertNull(store.currentTokens())
            saveProcessMarker()
        }

    @Test
    fun clearedPairStaysEmpty() =
        runBlocking {
            requirePhase("cleared")
            assertDifferentProcess()
            assertNull(createStore().currentTokens())
            assertTrue(marker.delete())
        }

    private fun createStore(): TokenStore =
        TokenStore(
            persistence = SessionTokenLocalDataSource(context = context, namespace = NAMESPACE),
        )

    private fun requirePhase(phase: String) {
        assumeTrue(
            "Run with scripts/verify_session_restart.py",
            runId != null &&
                InstrumentationRegistry.getArguments().getString("sessionRestartPhase") == phase,
        )
        Log.i("SessionRestartTest", "run=$runId phase=$phase pid=${Process.myPid()}")
    }

    private fun assertDifferentProcess() {
        val previous = marker.readText().split(':')
        assertEquals(runId, previous[0])
        assertNotEquals(previous[1].toInt(), Process.myPid())
    }

    private fun saveProcessMarker() {
        marker.writeText("$runId:${Process.myPid()}")
    }

    private companion object {
        const val NAMESPACE = "https://session-restart-fixture.test/api/"
        const val OLD_ACCESS = "restart-fixture-old-access"
        const val OLD_REFRESH = "restart-fixture-old-refresh"
        const val NEW_ACCESS = "restart-fixture-new-access"
        const val NEW_REFRESH = "restart-fixture-new-refresh"
    }
}
