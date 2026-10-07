package com.yapp.plus.core.designsystem.component.toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappToast(
    text: String,
    modifier: Modifier = Modifier,
    color: YappToastColor = YappToastColor.Dark,
    type: YappToastType = YappToastType.Default,
    showIcon: Boolean = true,
) {
    val shape = RoundedCornerShape(12.dp)
    val statusColor =
        when (type) {
            YappToastType.Default -> YappColor.success
            YappToastType.Error -> YappColor.error
        }
    val backgroundColor =
        when (color) {
            YappToastColor.Dark -> YappColor.toastBackgroundDark
            YappToastColor.White -> YappColor.white
        }
    val textColor =
        when {
            type == YappToastType.Error -> YappColor.error
            color == YappToastColor.Dark -> YappColor.white
            else -> YappColor.black
        }
    val iconResource =
        when (type) {
            YappToastType.Default -> R.drawable.ic_yapp_toast_success
            YappToastType.Error -> R.drawable.ic_yapp_toast_error
        }

    Row(
        modifier =
            modifier
                .widthIn(max = 320.dp)
                .defaultMinSize(minHeight = 40.dp)
                .shadow(
                    elevation = if (color == YappToastColor.Dark) 8.dp else 4.dp,
                    shape = shape,
                    ambientColor = YappColor.toastShadow,
                    spotColor = YappColor.toastShadow,
                ).clip(shape)
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showIcon) {
            Image(
                painter = painterResource(iconResource),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(statusColor),
            )
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = YappTypography.label2Medium,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 420)
@Composable
private fun YappToastPreview() {
    Column(
        modifier =
            Modifier
                .background(YappColor.white)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        YappToast(text = "요청한 작업을 완료했어요.")
        YappToast(text = "요청한 작업을 완료했어요.", color = YappToastColor.White)
        YappToast(text = "다시 시도해 주세요.", type = YappToastType.Error)
        Box(
            modifier =
                Modifier
                    .background(YappColor.textPrimary, RoundedCornerShape(16.dp))
                    .padding(16.dp),
        ) {
            YappToast(
                text = "다시 시도해 주세요.",
                color = YappToastColor.White,
                type = YappToastType.Error,
            )
        }
    }
}
