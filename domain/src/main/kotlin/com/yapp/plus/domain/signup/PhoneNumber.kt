package com.yapp.plus.domain.signup

@JvmInline
value class PhoneNumber(
    val value: String,
) {
    val isProvided: Boolean
        get() = value.isNotBlank()
}
