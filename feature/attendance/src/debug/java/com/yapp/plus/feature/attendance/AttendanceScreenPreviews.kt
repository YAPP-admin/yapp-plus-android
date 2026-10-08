package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Preview(name = "Home", widthDp = 393, heightDp = 692)
@Preview(name = "Compact home", widthDp = 320, heightDp = 560)
@Composable
private fun AttendanceHomeScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceHomeScreen() }
}

@Preview(name = "Board", widthDp = 393, heightDp = 692)
@Composable
private fun AttendanceBoardScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceBoardScreen() }
}

@Preview(name = "Header", widthDp = 393, showBackground = true)
@Composable
private fun AttendanceHeaderPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceHeader(
            title = stringResource(R.string.attendance_my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.attendance_my_settings),
        )
    }
}
