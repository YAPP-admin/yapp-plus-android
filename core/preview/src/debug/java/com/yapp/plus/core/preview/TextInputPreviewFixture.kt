package com.yapp.plus.core.preview

data class TextInputPreviewFixture(
    val value: String,
    val isEnabled: Boolean = true,
    val isFocused: Boolean = false,
    val hasError: Boolean = false,
)
