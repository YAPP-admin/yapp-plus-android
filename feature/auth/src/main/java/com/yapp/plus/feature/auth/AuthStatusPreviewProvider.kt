package com.yapp.plus.feature.auth

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class AuthStatusPreviewProvider : PreviewParameterProvider<AuthStatus> {
    override val values = AuthStatus.entries.asSequence()
}
