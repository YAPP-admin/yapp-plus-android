package com.yapp.plus.domain.signup

@JvmInline
value class PhoneNumber(
    val value: String,
) {
    val isProvided: Boolean
        get() = value.isNotBlank()

    fun toE164OrNull(): String? {
        val normalized = value.trim()
        if (normalized.any { !it.isDigit() && it !in ALLOWED_FORMAT_CHARACTERS }) return null
        val compact = normalized.filterNot(Char::isWhitespace).filterNot { it in SEPARATOR_CHARACTERS }
        if (compact.matches(E164_PATTERN)) return compact
        if ('+' in compact) return null

        val localNumber = compact.filter(Char::isDigit)
        if (localNumber.matches(KOREAN_MOBILE_PATTERN)) {
            return "+82${localNumber.drop(1)}"
        }
        return null
    }

    private companion object {
        val E164_PATTERN = Regex("\\+[1-9]\\d{7,14}")
        val KOREAN_MOBILE_PATTERN = Regex("01[016789]\\d{7,8}")
        val ALLOWED_FORMAT_CHARACTERS = setOf('+', '-', '(', ')', ' ')
        val SEPARATOR_CHARACTERS = setOf('-', '(', ')')
    }
}
