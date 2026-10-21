package com.yapp.plus.domain.signup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberTest {
    @Test
    fun isProvidedOnlyWhenValueIsNotBlank() {
        assertFalse(PhoneNumber("").isProvided)
        assertFalse(PhoneNumber("   ").isProvided)
        assertTrue(PhoneNumber("임시 번호").isProvided)
    }
}
