package com.yapp.plus.feature.main.home.attendance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(widthDp = 337)
@Composable
fun HomeCodeInputPreview() {
    YappTheme { HomeCodeInput("1", {}, isError = false, enabled = true, onConfirm = {}) }
}

@Preview(widthDp = 54)
@Composable
fun HomeCodeDigitPreview() {
    YappTheme { HomeCodeDigit('1', isActive = false, isError = true) }
}

@Preview(widthDp = 350, heightDp = 180)
@Composable
fun HomeCompletionGraphicPreview() {
    YappTheme { HomeCompletionGraphic() }
}

@Preview(widthDp = 150, heightDp = 150)
@Composable
fun HomeConfettiPreview() {
    YappTheme { HomeConfetti(isLeft = true, scale = 1f, time = 1000f, canAnimate = true) }
}
