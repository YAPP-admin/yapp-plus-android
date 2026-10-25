package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yapp.plus.feature.main.my.model.MyUiState

@Composable
fun MyRoute(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isHistoryExpanded by rememberSaveable { mutableStateOf(false) }
    MyScreen(
        state = MyUiState(isAttendanceHistoryExpanded = isHistoryExpanded),
        onAttendanceHistoryClick = { isHistoryExpanded = !isHistoryExpanded },
        onSettingsClick = onSettingsClick,
        modifier = modifier,
    )
}
