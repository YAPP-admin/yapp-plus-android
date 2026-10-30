package com.yapp.plus.feature.main.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.main.YappDetailHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.my.components.MyAttendanceHistory
import com.yapp.plus.feature.main.my.components.MyRequestState
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState

@Composable
fun MyAttendanceHistoryScreen(
    state: MyAttendanceHistoryState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    absentCount: Int? = null,
) {
    Column(modifier.fillMaxSize().background(YappMainColor.surface)) {
        YappDetailHeader(
            title = stringResource(R.string.my_attendance_history_title),
            backDescription = stringResource(R.string.my_back),
            onBack = onBack,
        )
        when (state) {
            MyAttendanceHistoryState.NotRequested, MyAttendanceHistoryState.Loading ->
                MyRequestState(
                    message = stringResource(R.string.my_attendance_history_loading),
                    isLoading = true,
                )
            MyAttendanceHistoryState.Error ->
                MyRequestState(
                    message = stringResource(R.string.my_attendance_history_error),
                    onRetry = onRetry,
                )
            is MyAttendanceHistoryState.Content ->
                MyAttendanceHistory(
                    state = state,
                    showAbsenceWarning =
                        absentCount != null && absentCount >= ABSENCE_WARNING_COUNT,
                    onLoadMore = onLoadMore,
                )
        }
    }
}

private const val ABSENCE_WARNING_COUNT = 10
