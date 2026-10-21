package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState

@Preview(name = "My page", widthDp = 393, heightDp = 692)
@Preview(name = "Tall My page", widthDp = 393, heightDp = 852)
@Preview(name = "Compact My page", widthDp = 320, heightDp = 560)
@Composable
private fun MyScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyScreen(
            state =
                MyProfileUiState.Content(
                    MyProfileUiModel(name = "홍길동", phoneNumber = "01012345678"),
                ),
        )
    }
}
