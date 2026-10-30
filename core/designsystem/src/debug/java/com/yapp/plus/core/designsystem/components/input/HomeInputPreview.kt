package com.yapp.plus.core.designsystem.components.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun HomeInputPreview() {
    YappTheme {
        YappTextField("", {}, placeholder = "사유를 입력해 주세요", variant = YappTextFieldVariant.Home)
    }
}
