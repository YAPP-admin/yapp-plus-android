package com.yapp.plus.core.designsystem.components.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(showBackground = true)
@Composable
private fun ProfileChipPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappChip(
            text = "활동회원",
            color = YappChipColor.Brand,
            size = YappChipSize.Profile,
            style = YappChipStyle.Fill,
        )
    }
}
