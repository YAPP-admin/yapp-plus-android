package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "My menu row", widthDp = 393)
@Composable
private fun MyMenuRowPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column(Modifier.background(YappMainColor.surface)) {
            MyMenuRow(title = "출석 내역", onClick = {})
            MyMenuRow(title = "펼친 출석 내역", onClick = {}, expanded = true)
            MyMenuRow(title = "이용문의", onClick = {})
        }
    }
}
