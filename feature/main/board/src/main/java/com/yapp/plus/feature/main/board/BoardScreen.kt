package com.yapp.plus.feature.main.board

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
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.components.BoardCategorySelector
import com.yapp.plus.feature.main.board.components.BoardEmptyState
import com.yapp.plus.feature.main.board.model.BoardCategory

@Composable
fun BoardScreen(modifier: Modifier = Modifier) {
    var selectedCategory by rememberSaveable { mutableStateOf(BoardCategory.All) }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        YappMainHeader(title = stringResource(R.string.board_title))
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
            BoardEmptyState(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(bottom = 32.dp),
            )
        }
    }
}
