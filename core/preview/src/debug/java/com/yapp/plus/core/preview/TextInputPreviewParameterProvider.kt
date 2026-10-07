package com.yapp.plus.core.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

open class TextInputPreviewParameterProvider(
    sampleValue: String = "Preview input",
) : PreviewParameterProvider<TextInputPreviewFixture> {
    override val values: Sequence<TextInputPreviewFixture> =
        sequenceOf(
            TextInputPreviewFixture(value = ""),
            TextInputPreviewFixture(value = "", isFocused = true),
            TextInputPreviewFixture(value = sampleValue),
            TextInputPreviewFixture(value = sampleValue, isEnabled = false),
            TextInputPreviewFixture(value = sampleValue, hasError = true),
        )
}
