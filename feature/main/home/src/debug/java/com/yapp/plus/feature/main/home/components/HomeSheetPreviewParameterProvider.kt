package com.yapp.plus.feature.main.home.components

import com.yapp.plus.core.preview.EnumPreviewParameterProvider
import com.yapp.plus.feature.main.home.model.HomeSheet

class HomeSheetPreviewParameterProvider :
    EnumPreviewParameterProvider<HomeSheet>(
        HomeSheet.entries.filter { it != HomeSheet.None },
    )
