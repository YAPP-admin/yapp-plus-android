package com.yapp.plus.feature.main.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.R

@Preview(widthDp = 240)
@Composable
private fun HomeSessionVenuePreview() {
    YappTheme {
        HomeSessionVenue(
            location = stringResource(R.string.attendance_home_demo_session_location),
        )
    }
}
