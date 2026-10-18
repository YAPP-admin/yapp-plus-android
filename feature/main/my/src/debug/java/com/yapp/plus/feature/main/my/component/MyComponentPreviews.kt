package com.yapp.plus.feature.main.my.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.R

@Preview(name = "Profile", widthDp = 353, showBackground = true)
@Composable
private fun MyProfileCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyProfileCard(
            name = stringResource(R.string.my_name),
            role = stringResource(R.string.my_role),
            generation = stringResource(R.string.my_generation),
        )
    }
}

@Preview(name = "Attendance summary", widthDp = 353, showBackground = true)
@Composable
private fun MySummaryCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { MySummaryCard() }
}

@Preview(name = "Attendance statistic", widthDp = 85, showBackground = true)
@Composable
private fun MyStatisticPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyStatistic(label = stringResource(R.string.my_present), count = 3)
    }
}

@Preview(name = "My menu item", widthDp = 353, showBackground = true)
@Composable
private fun MyMenuItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyMenuItem(title = stringResource(R.string.my_history))
    }
}
