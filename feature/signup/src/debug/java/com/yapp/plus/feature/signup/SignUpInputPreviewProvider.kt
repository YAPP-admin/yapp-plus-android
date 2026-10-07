package com.yapp.plus.feature.signup

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yapp.plus.core.preview.EnumPreviewParameterProvider
import com.yapp.plus.core.preview.TextInputPreviewFixture
import com.yapp.plus.core.preview.TextInputPreviewParameterProvider

internal class SignUpInputPreviewProvider :
    PreviewParameterProvider<Pair<SignUpStep, TextInputPreviewFixture>> {
    override val values =
        EnumPreviewParameterProvider(SignUpStep.entries).values.flatMap { step ->
            TextInputPreviewParameterProvider().values.map { step to it }
        }
}
