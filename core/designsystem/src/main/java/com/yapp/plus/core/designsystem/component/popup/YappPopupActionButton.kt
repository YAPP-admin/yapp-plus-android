package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
internal fun YappPopupActionButton(
    text: String,
    primary: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(if (primary) 12.dp else 10.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = shape,
        color = if (primary) YappColor.primary else YappColor.white,
        border = if (primary) null else BorderStroke(1.dp, YappColor.divider),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = YappTypography.body1NormalBold,
                color = if (primary) YappColor.white else YappColor.primary,
            )
        }
    }
}
