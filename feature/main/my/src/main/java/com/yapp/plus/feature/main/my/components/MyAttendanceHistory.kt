package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R

sealed interface MyAttendanceHistoryState {
    data object NotRequested : MyAttendanceHistoryState

    data object Loading : MyAttendanceHistoryState

    data object Empty : MyAttendanceHistoryState

    data object Error : MyAttendanceHistoryState

    @JvmInline
    value class Content(
        val items: List<MyAttendanceHistoryItem>,
    ) : MyAttendanceHistoryState
}

data class MyAttendanceHistoryItem(
    val sessionName: String,
    val checkedInAtLabel: String,
)

@Composable
fun MyAttendanceHistory(
    state: MyAttendanceHistoryState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    when (state) {
        MyAttendanceHistoryState.NotRequested -> Unit
        MyAttendanceHistoryState.Loading ->
            Box(
                modifier = modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(color = YappMainColor.brand)
                    YappText(
                        text = stringResource(R.string.my_attendance_history_loading),
                        style = YappTypography.body2NormalRegular,
                        color = YappMainColor.textSecondary,
                    )
                }
            }

        MyAttendanceHistoryState.Empty -> HistoryMessage(stringResource(R.string.my_attendance_history_empty), modifier)
        MyAttendanceHistoryState.Error ->
            Row(
                modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    text = stringResource(R.string.my_attendance_history_error),
                    style = YappTypography.body2NormalRegular,
                    color = YappMainColor.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onRetry) {
                    YappText(
                        text = stringResource(R.string.my_retry),
                        style = YappTypography.label1NormalMedium,
                        color = YappMainColor.brand,
                    )
                }
            }

        is MyAttendanceHistoryState.Content ->
            if (state.items.isEmpty()) {
                HistoryMessage(stringResource(R.string.my_attendance_history_empty), modifier)
            } else {
                Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.items.forEach { item -> MyAttendanceHistoryItem(item) }
                }
            }
    }
}

@Composable
private fun HistoryMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    YappText(
        text = message,
        style = YappTypography.body2NormalRegular,
        color = YappMainColor.textSecondary,
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
    )
}

@Composable
private fun MyAttendanceHistoryItem(item: MyAttendanceHistoryItem) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        YappText(
            text = item.sessionName,
            style = YappTypography.body2NormalMedium,
            color = YappMainColor.textPrimary,
        )
        YappText(
            text = item.checkedInAtLabel,
            style = YappTypography.label1NormalMedium,
            color = YappMainColor.textSecondary,
        )
    }
}
