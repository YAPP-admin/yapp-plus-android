package com.yapp.plus.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor

@Composable
fun AttendanceBoardScreen(modifier: Modifier = Modifier) {
    var selectedCategory by rememberSaveable { mutableStateOf(BoardCategory.All) }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        AttendanceHeader(title = stringResource(R.string.attendance_board_title))
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
        ) {
            BoardCategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
            )
            AttendanceBoardEmptyState(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(bottom = 32.dp),
            )
        }
    }
}
