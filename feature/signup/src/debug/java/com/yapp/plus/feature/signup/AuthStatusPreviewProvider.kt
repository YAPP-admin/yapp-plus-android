package com.yapp.plus.feature.signup

import com.yapp.plus.core.preview.EnumPreviewParameterProvider

internal class AuthStatusPreviewProvider :
    EnumPreviewParameterProvider<AuthStatus>(AuthStatus.entries)
