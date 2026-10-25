package com.yapp.plus.core.designsystem.components.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

private val navigationItemWidth = 70.dp
private val navigationItemMinimumHeight = 52.dp
private val navigationIconSize = 24.dp

@Composable
fun YappBottomNavigationItem(
    label: String,
    @DrawableRes iconResource: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedIconColor: Color = YappMainColor.textSecondary,
) {
    val contentColor = if (isSelected) YappMainColor.textSecondary else YappMainColor.textFaint
    Column(
        modifier =
            modifier
                .width(navigationItemWidth)
                .heightIn(min = navigationItemMinimumHeight)
                .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier = Modifier.size(navigationIconSize),
            tint = if (isSelected) selectedIconColor else YappMainColor.textFaint,
        )
        YappText(
            text = label,
            style = YappTypography.scaleT2Bold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
