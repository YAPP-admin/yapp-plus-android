package com.yapp.plus.core.designsystem.components.refresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Pull to refresh", widthDp = 393, heightDp = 320, showBackground = true)
@Composable
private fun YappPullToRefreshBoxPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappPullToRefreshBox(
            isRefreshing = true,
            onRefresh = {},
            modifier =
                Modifier
                    .width(393.dp)
                    .height(320.dp)
                    .background(YappMainColor.basement),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("공용 새로고침 콘텐츠", color = YappMainColor.textPrimary)
            }
        }
    }
}
