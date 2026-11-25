package com.yapp.plus.feature.signup

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal object SignUpPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val number = text.text
        if (!number.startsWith("01") || number.length !in 4..11 || number.any { it !in '0'..'9' }) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val middleEnd = if (number.length == 10 && !number.startsWith("010")) 6 else 7
        val separators = listOf(3, middleEnd).filter { it < number.length }
        val formatted =
            buildString {
                for ((index, digit) in number.withIndex()) {
                    if (index in separators) append('-')
                    append(digit)
                }
            }
        val offsetMapping =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val inserted = separators.count { it <= offset }
                    return offset + inserted
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val inserted = separators.withIndex().count { (index, position) -> position + index < offset }
                    return offset - inserted
                }
            }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
