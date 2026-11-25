package com.yapp.plus.feature.signup

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class SignUpPhoneVisualTransformationTest {
    @Test
    fun formatsLocalMobileNumbersAndPreservesCursorOffsets() {
        val examples =
            mapOf(
                "0101" to "010-1",
                "0101234567" to "010-1234-567",
                "01012345678" to "010-1234-5678",
                "0111234567" to "011-123-4567",
            )

        for ((raw, expected) in examples) {
            val result = SignUpPhoneVisualTransformation.filter(AnnotatedString(raw))
            assertEquals(expected, result.text.text)
            for (offset in 0..raw.length) {
                val transformed = result.offsetMapping.originalToTransformed(offset)
                assertEquals(offset, result.offsetMapping.transformedToOriginal(transformed))
            }
        }
    }

    @Test
    fun keepsInternationalAndAlreadyFormattedNumbersIntact() {
        val examples = listOf("", "+821012345678", "010-1234-5678", "010 1234 5678")
        for (raw in examples) {
            val result = SignUpPhoneVisualTransformation.filter(AnnotatedString(raw))
            assertEquals(raw, result.text.text)
            assertEquals(raw.length, result.offsetMapping.originalToTransformed(raw.length))
        }
    }
}
