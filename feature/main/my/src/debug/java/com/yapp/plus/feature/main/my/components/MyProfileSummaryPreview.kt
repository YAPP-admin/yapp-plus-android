package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "My profile summary", widthDp = 393)
@Composable
private fun MyProfileSummaryPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyProfileSummary(
            state = MyProfileSummaryState.Content("YAPP 테스트 계정"),
            modifier = Modifier.background(YappMainColor.surface),
            onRetry = {},
        )
    }
}
