package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyRoute(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyScreen(
        onSettingsClick = onSettingsClick,
        modifier = modifier,
    )
}
