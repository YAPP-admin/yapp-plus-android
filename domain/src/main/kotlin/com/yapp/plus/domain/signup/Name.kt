package com.yapp.plus.domain.signup

@JvmInline
value class Name(
    val value: String,
) {
    val isProvided: Boolean
        get() = value.isNotBlank()
}
