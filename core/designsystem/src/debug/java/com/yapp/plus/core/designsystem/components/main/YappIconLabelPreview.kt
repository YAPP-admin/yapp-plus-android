package com.yapp.plus.core.designsystem.components.main

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Icon label", showBackground = true)
@Composable
private fun YappIconLabelPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column {
            YappIconLabel("DMC 첨단산업센터", R.drawable.ic_map_pin)
            YappIconLabel(
                text = "DMC 첨단산업센터\n서울특별시 마포구 월드컵북로 396 2층",
                iconResource = R.drawable.ic_map_pin,
                maxLines = Int.MAX_VALUE,
            )
        }
    }
}
