package com.yapp.plus.feature.auth

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class SignUpStepPreviewProvider : PreviewParameterProvider<SignUpStep> {
    override val values = sequenceOf(SignUpStep.Name, SignUpStep.Phone)
}
