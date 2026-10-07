package com.yapp.plus.core.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

open class EnumPreviewParameterProvider<T : Enum<T>>(
    entries: Iterable<T>,
) : PreviewParameterProvider<T> {
    override val values: Sequence<T> = entries.asSequence()
}
