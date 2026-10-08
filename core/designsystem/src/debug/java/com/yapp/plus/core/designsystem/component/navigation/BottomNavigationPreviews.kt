package com.yapp.plus.core.designsystem.component.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Bottom navigation", widthDp = 393)
@Preview(name = "Compact bottom navigation", widthDp = 320)
@Composable
private fun YappBottomNavigationPreview(
    @PreviewParameter(BottomNavigationPreviewParameterProvider::class)
    selectedItem: BottomNavigationPreviewItem,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        YappBottomNavigation {
            for (item in BottomNavigationPreviewItem.entries) {
                YappBottomNavigationItem(
                    label = stringResource(item.labelResource),
                    iconResource = item.iconResource,
                    isSelected = item == selectedItem,
                    onClick = {},
                )
            }
        }
    }
}

@Preview(name = "Bottom navigation item", showBackground = true)
@Composable
private fun YappBottomNavigationItemPreview(
    @PreviewParameter(BottomNavigationPreviewParameterProvider::class)
    item: BottomNavigationPreviewItem,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Row {
            for (isSelected in listOf(true, false)) {
                YappBottomNavigationItem(
                    label = stringResource(item.labelResource),
                    iconResource = item.iconResource,
                    isSelected = isSelected,
                    onClick = {},
                )
            }
        }
    }
}
