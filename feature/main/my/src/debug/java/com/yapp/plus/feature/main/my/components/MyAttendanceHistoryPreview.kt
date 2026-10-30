package com.yapp.plus.feature.main.my.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.MyPreviewData

@Preview(widthDp = 393, heightDp = 702)
@Composable
private fun MyAttendanceHistoryPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MyAttendanceHistory(MyPreviewData.history, showAbsenceWarning = true, onLoadMore = {})
    }
}
