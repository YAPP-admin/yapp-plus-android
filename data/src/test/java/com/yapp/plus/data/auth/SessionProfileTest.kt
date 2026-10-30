package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.data.auth.datasource.session.SessionTokenLocalDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionProfileTest {
    @Test fun nameSurvivesTokenRotationAndProcessRestoration() =
        runTest {
            val storage = FakeSessionRecordStore()
            val source = SessionTokenLocalDataSource(storage)
            val tokens = Tokens("access", "refresh")
            source.updateTokens(tokens)
            assertTrue(source.updateName(tokens.storageKey, "김야뿌"))
            source.updateTokens(Tokens("renewed-access", "renewed-refresh", tokens.storageKey))
            val restored = SessionTokenLocalDataSource(storage)
            assertEquals("김야뿌", restored.fetchName())
            assertEquals(tokens.storageKey, restored.fetchTokens()!!.storageKey)
            assertEquals("renewed-access", restored.fetchTokens()!!.accessToken)
        }

    @Test fun newLoginAndLogoutClearNameAndRejectOldWrites() =
        runTest {
            val source = SessionTokenLocalDataSource(FakeSessionRecordStore())
            val old = Tokens("old-access", "old-refresh")
            source.updateTokens(old)
            source.updateName(old.storageKey, "이전 계정")
            source.updateTokens(Tokens("new-access", "new-refresh"))
            assertNull(source.fetchName())
            assertFalse(source.updateName(old.storageKey, "지연된 응답"))
            source.updateTokens(null)
            assertNull(source.fetchName())
            assertNull(source.fetchTokens())
        }

    @Test fun legacySessionWithoutProfileRemainsValid() =
        runTest {
            val storage = FakeSessionRecordStore()
            storage.record = SessionTokenRecord("legacy-access", "legacy-refresh")
            val source = SessionTokenLocalDataSource(storage)
            assertEquals("legacy-access", source.fetchTokens()!!.accessToken)
            assertNull(source.fetchName())
        }
}
