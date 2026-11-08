package com.yapp.plus.feature.main.my.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.MyPreviewData
import com.yapp.plus.feature.main.my.model.MyProfileUiState

@Preview
@Composable
private fun MyProfilePreview() {
    YappTheme { MyProfile(MyPreviewData.profile) }
}

@Preview(name = "Profile loading", widthDp = 393)
@Composable
private fun MyProfileLoadingPreview() {
    YappTheme {
        MyProfileSection(
            state = MyProfileUiState.Loading(savedName = MyPreviewData.profile.name),
            onRetry = {},
        )
    }
}

@Preview(name = "Profile error", widthDp = 393)
@Composable
private fun MyProfileErrorPreview() {
    YappTheme { MyProfileSection(state = MyProfileUiState.Error(), onRetry = {}) }
}
