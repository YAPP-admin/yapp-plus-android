package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.community.R

@Preview(name = "Community gatherings", widthDp = 393, heightDp = 756, showBackground = true)
@Composable
private fun CommunityGatheringsPreview() {
    YappTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(YappMainColor.surface)
                .safeDrawingPadding(),
        ) {
            YappMainHeader(title = stringResource(R.string.community_title))
            CommunityTabs(selectedSection = CommunitySection.Gatherings, onSelect = {})
            CommunityGatherings()
        }
    }
}
