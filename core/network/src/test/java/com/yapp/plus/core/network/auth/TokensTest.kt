package com.yapp.plus.core.network.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class TokensTest {
    @Test
    fun refreshPreservesSessionIdentityAndProfileStorageKey() {
        val original = Tokens("access", "refresh")
        val renewed = original.renewedWith(Tokens("new-access", "new-refresh"))
        assertSame(original.sessionId, renewed.sessionId)
        assertEquals(original.storageKey, renewed.storageKey)
        assertEquals("new-access", renewed.accessToken)
        assertEquals("new-refresh", renewed.refreshToken)
    }
}
