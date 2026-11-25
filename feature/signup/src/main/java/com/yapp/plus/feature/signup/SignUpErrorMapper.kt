package com.yapp.plus.feature.signup

import androidx.annotation.StringRes
import com.yapp.plus.domain.auth.RegistrationFieldError

object SignUpErrorMapper {
    @StringRes
    fun toNameErrorMessageRes(reason: RegistrationFieldError.Reason): Int =
        when (reason) {
            RegistrationFieldError.Reason.Required -> R.string.auth_name_required
            RegistrationFieldError.Reason.TooLong -> R.string.auth_name_too_long
            else -> R.string.auth_name_invalid
        }

    @StringRes
    fun toPhoneNumberErrorMessageRes(reason: RegistrationFieldError.Reason): Int =
        when (reason) {
            RegistrationFieldError.Reason.Required -> R.string.auth_phone_required
            RegistrationFieldError.Reason.AlreadyExists -> R.string.auth_phone_already_exists
            else -> R.string.auth_phone_invalid
        }
}

@StringRes
fun RegistrationFieldError.Reason.toNameErrorMessageRes(): Int = SignUpErrorMapper.toNameErrorMessageRes(this)

@StringRes
fun RegistrationFieldError.Reason.toPhoneNumberErrorMessageRes(): Int =
    SignUpErrorMapper.toPhoneNumberErrorMessageRes(this)
