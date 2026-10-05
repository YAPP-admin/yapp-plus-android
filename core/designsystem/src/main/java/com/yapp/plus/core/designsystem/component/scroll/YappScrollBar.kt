package com.yapp.plus.core.designsystem.component.scroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappColor

@Composable
fun YappScrollBar(
    modifier: Modifier = Modifier,
    size: YappScrollBarSize = YappScrollBarSize.Normal,
    percent: YappScrollBarPercent = YappScrollBarPercent.Full,
    position: YappScrollBarPosition = YappScrollBarPosition.Top,
    contentDescription: String? = null
) {
    val semanticsModifier = if (contentDescription == null) {
        Modifier
    } else {
        Modifier.semantics { this.contentDescription = contentDescription }
    }
    val alignment = when (position) {
        YappScrollBarPosition.Top -> Alignment.TopCenter
        YappScrollBarPosition.Center -> Alignment.Center
        YappScrollBarPosition.Bottom -> Alignment.BottomCenter
    }

    Box(
        modifier = modifier
            .width(size.containerWidth)
            .height(106.dp)
            .then(semanticsModifier)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(alignment)
                .width(size.width)
                .fillMaxHeight(percent.fraction)
                .clip(CircleShape)
                .background(YappColor.scrollIndicator)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 320)
@Composable
private fun YappScrollBarPreview() {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Normal")
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            YappScrollBar(percent = YappScrollBarPercent.Quarter)
            YappScrollBar(
                percent = YappScrollBarPercent.Half,
                position = YappScrollBarPosition.Center
            )
            YappScrollBar(
                percent = YappScrollBarPercent.ThreeQuarters,
                position = YappScrollBarPosition.Bottom
            )
            YappScrollBar(percent = YappScrollBarPercent.Full)
        }
        Text("Small")
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            YappScrollBar(size = YappScrollBarSize.Small, percent = YappScrollBarPercent.Quarter)
            YappScrollBar(
                size = YappScrollBarSize.Small,
                percent = YappScrollBarPercent.Half,
                position = YappScrollBarPosition.Center
            )
            YappScrollBar(
                size = YappScrollBarSize.Small,
                percent = YappScrollBarPercent.ThreeQuarters,
                position = YappScrollBarPosition.Bottom
            )
            YappScrollBar(size = YappScrollBarSize.Small)
        }
    }
}
