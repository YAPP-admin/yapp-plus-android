package com.yapp.plus.domain.signup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NameTest {
    @Test
    fun isProvidedOnlyWhenValueIsNotBlank() {
        assertFalse(Name("").isProvided)
        assertFalse(Name("   ").isProvided)
        assertTrue(Name("이름").isProvided)
    }
}
