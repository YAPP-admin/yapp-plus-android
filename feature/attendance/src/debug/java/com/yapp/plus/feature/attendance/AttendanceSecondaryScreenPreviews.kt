package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Schedule", widthDp = 393, heightDp = 692)
@Preview(name = "Compact schedule", widthDp = 320, heightDp = 560)
@Composable
private fun AttendanceScheduleScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceScheduleScreen() }
}

@Preview(name = "My page", widthDp = 393, heightDp = 692)
@Preview(name = "Compact My page", widthDp = 320, heightDp = 560)
@Composable
private fun AttendanceMyScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceMyScreen() }
}
