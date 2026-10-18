package com.yapp.plus.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Home", widthDp = 393, heightDp = 692)
@Preview(name = "Tall home", widthDp = 393, heightDp = 852)
@Preview(name = "Compact home", widthDp = 320, heightDp = 560)
@Composable
private fun HomeScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { HomeScreen() }
}
