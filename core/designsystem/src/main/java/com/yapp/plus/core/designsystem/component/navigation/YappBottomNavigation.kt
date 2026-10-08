package com.yapp.plus.core.designsystem.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor

private val navigationDividerHeight = 1.dp
private val navigationHorizontalPadding = 16.dp
private val navigationVerticalPadding = 8.dp

@Composable
fun YappBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(YappMainColor.surface),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(navigationDividerHeight)
                    .background(YappMainColor.divider),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(
                        horizontal = navigationHorizontalPadding,
                        vertical = navigationVerticalPadding,
                    ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
