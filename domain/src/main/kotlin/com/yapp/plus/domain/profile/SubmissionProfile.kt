package com.yapp.plus.domain.profile

import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

data class SubmissionProfile(
    val name: Name,
    val phoneNumber: PhoneNumber,
)
