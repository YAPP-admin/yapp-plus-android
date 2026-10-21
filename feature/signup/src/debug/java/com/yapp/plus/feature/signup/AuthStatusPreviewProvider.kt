package com.yapp.plus.feature.signup

import com.yapp.plus.core.preview.EnumPreviewParameterProvider
import com.yapp.plus.feature.signup.model.AuthStatus

internal class AuthStatusPreviewProvider : EnumPreviewParameterProvider<AuthStatus>(AuthStatus.entries)
