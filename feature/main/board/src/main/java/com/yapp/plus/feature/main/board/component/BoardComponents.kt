package com.yapp.plus.feature.main.board.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.R
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun BoardCategorySelector(
    selectedCategory: BoardCategory,
    onCategorySelected: (BoardCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (category in BoardCategory.entries) {
                BoardCategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                )
            }
        }
        HorizontalDivider(color = YappMainColor.divider)
    }
}

@Composable
fun BoardCategoryChip(
    category: BoardCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(IntrinsicSize.Min)
                .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
    ) {
        YappText(
            stringResource(category.labelResource),
            YappTypography.body1NormalBold,
            if (isSelected) YappMainColor.textPrimary else YappMainColor.textFaint,
            modifier =
                Modifier
                    .padding(horizontal = 6.dp)
                    .padding(top = 8.dp, bottom = 10.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        if (isSelected) YappMainColor.textPrimary else YappMainColor.surface,
                    ),
        )
    }
}

@Composable
fun BoardEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.graphic_none),
            contentDescription = null,
            modifier = Modifier.size(width = 90.dp, height = 74.dp),
        )
        YappText(
            stringResource(R.string.board_empty),
            YappTypography.body1NormalRegular,
            YappMainColor.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
