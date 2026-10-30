package com.yapp.plus.feature.main.my.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.model.MyProfileUiModel

@Preview
@Composable
private fun MyProfilePreview() {
    YappTheme {
        MyProfile(
            MyProfileUiModel(
                name = "김야뿌",
                role = "활동회원",
                generation = "28기",
                position = "Designer",
            ),
        )
    }
}
