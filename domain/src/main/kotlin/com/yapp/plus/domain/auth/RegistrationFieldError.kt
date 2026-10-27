package com.yapp.plus.domain.auth

sealed interface RegistrationFieldError {
    val reason: Reason

    data class Name(
        override val reason: Reason,
    ) : RegistrationFieldError

    data class PhoneNumber(
        override val reason: Reason,
    ) : RegistrationFieldError

    enum class Reason {
        Required,
        InvalidFormat,
        TooLong,
        AlreadyExists,
        Unknown,
    }
}
