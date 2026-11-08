package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoReceiptUiModel

@Composable
internal fun HomeTeamPhotoReceiptDetails(receipt: HomeTeamPhotoReceiptUiModel) {
    val facts =
        listOf(
            R.string.home_team_result_receipt_team to receipt.teamName,
            R.string.home_team_result_receipt_time to receipt.uploadTimeLabel,
            R.string.home_team_result_receipt_count to
                stringResource(
                    R.string.home_team_result_count,
                    receipt.attendeeCount,
                    receipt.teamMemberCount,
                ),
            R.string.home_team_result_receipt_members to receipt.attendeeNamesLabel,
        )
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .background(YappMainColor.layerFill, RoundedCornerShape(8.dp))
            .padding(18.dp),
    ) {
        val maxValueWidth = maxWidth * 0.65f
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            for ((label, value) in facts) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    YappText(stringResource(label), YappTypography.scaleT4Medium, YappHomeColor.muted)
                    Canvas(Modifier.weight(1f).height(1.dp)) {
                        drawLine(
                            color = YappHomeColor.border,
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx())),
                        )
                    }
                    YappText(
                        text = value,
                        style = YappTypography.scaleT4Medium,
                        color = YappHomeColor.text,
                        modifier = Modifier.widthIn(max = maxValueWidth),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
