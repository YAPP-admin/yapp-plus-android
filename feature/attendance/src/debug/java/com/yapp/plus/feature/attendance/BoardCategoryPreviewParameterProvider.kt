package com.yapp.plus.feature.attendance

import com.yapp.plus.core.preview.EnumPreviewParameterProvider
import com.yapp.plus.feature.attendance.model.BoardCategory

internal class BoardCategoryPreviewParameterProvider :
    EnumPreviewParameterProvider<BoardCategory>(BoardCategory.entries)
