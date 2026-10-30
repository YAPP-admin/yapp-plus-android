package com.yapp.plus.feature.signup

import androidx.annotation.StringRes
import com.yapp.plus.domain.auth.RegistrationFieldError

@StringRes
fun RegistrationFieldError.Reason.toNameErrorMessageRes(): Int =
    when (this) {
        RegistrationFieldError.Reason.Required -> R.string.auth_name_required
        RegistrationFieldError.Reason.TooLong -> R.string.auth_name_too_long
        else -> R.string.auth_name_invalid
    }

@StringRes
fun RegistrationFieldError.Reason.toPhoneNumberErrorMessageRes(): Int =
    when (this) {
        RegistrationFieldError.Reason.Required -> R.string.auth_phone_required
        RegistrationFieldError.Reason.AlreadyExists -> R.string.auth_phone_already_exists
        else -> R.string.auth_phone_invalid
    }
