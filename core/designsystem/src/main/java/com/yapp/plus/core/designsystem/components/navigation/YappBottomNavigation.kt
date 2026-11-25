package com.yapp.plus.core.designsystem.components.navigation

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme

private val navigationDividerHeight = 1.dp
private val navigationHorizontalPadding = 20.dp

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
                    .background(YappMainColor.border),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(
                        start = navigationHorizontalPadding,
                        end = navigationHorizontalPadding,
                        top = 7.dp,
                        bottom = 4.dp,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun YappBottomNavigationPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappBottomNavigation {
            YappBottomNavigationItem(
                label = "홈",
                iconResource = R.drawable.ic_navigation_house,
                isSelected = true,
                onClick = {},
            )
            YappBottomNavigationItem(
                label = "일정",
                iconResource = R.drawable.ic_navigation_calendar,
                isSelected = false,
                onClick = {},
            )
            YappBottomNavigationItem(
                label = "게시판",
                iconResource = R.drawable.ic_navigation_presentation,
                isSelected = false,
                onClick = {},
            )
            YappBottomNavigationItem(
                label = "마이",
                iconResource = R.drawable.ic_navigation_user,
                isSelected = false,
                onClick = {},
            )
        }
    }
}
