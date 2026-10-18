package com.yapp.plus.core.designsystem.component.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Icon label", showBackground = true)
@Composable
private fun YappIconLabelPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappIconLabel("DMC 첨단산업센터", R.drawable.ic_map_pin)
    }
}
