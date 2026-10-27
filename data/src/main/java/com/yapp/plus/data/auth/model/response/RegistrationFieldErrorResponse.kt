package com.yapp.plus.data.auth.model.response

import com.yapp.plus.domain.auth.RegistrationFieldError
import kotlinx.serialization.Serializable

@Serializable
internal data class RegistrationFieldErrorResponse(
    val field: String,
    val code: String,
) {
    fun toDomain(): RegistrationFieldError? {
        val reason =
            when (code) {
                "REQUIRED" -> RegistrationFieldError.Reason.Required
                "INVALID_FORMAT" -> RegistrationFieldError.Reason.InvalidFormat
                "TOO_LONG" -> RegistrationFieldError.Reason.TooLong
                "ALREADY_EXISTS" -> RegistrationFieldError.Reason.AlreadyExists
                else -> RegistrationFieldError.Reason.Unknown
            }
        return when (field) {
            "name" -> RegistrationFieldError.Name(reason)
            "phoneNumber" -> RegistrationFieldError.PhoneNumber(reason)
            else -> null
        }
    }
}
