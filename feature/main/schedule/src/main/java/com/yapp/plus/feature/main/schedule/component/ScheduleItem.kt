package com.yapp.plus.feature.main.schedule.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.chip.YappChip
import com.yapp.plus.core.designsystem.component.chip.YappChipColor
import com.yapp.plus.core.designsystem.component.chip.YappChipSize
import com.yapp.plus.core.designsystem.component.chip.YappChipStyle
import com.yapp.plus.core.designsystem.component.main.YappIconLabel
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun ScheduleItem(
    session: ScheduleSessionUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        color =
                            if (session.status.isToday) {
                                YappMainColor.background
                            } else {
                                YappMainColor.surface
                            },
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 8.dp, vertical = 8.dp)
                    .alpha(
                        if (session.status.isPast) {
                            0.5f
                        } else {
                            1f
                        },
                    ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YappText(
                session.dateLabel,
                YappTypography.label1NormalBold,
                YappMainColor.textPrimary,
                modifier = Modifier.width(70.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    YappText(
                        session.name,
                        YappTypography.label1NormalBold,
                        YappMainColor.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    session.status.labelResource?.let { labelRes ->
                        YappChip(
                            text = stringResource(labelRes),
                            color = YappChipColor.CoolNeutral,
                            size = YappChipSize.Small,
                            style = YappChipStyle.Weak,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    YappText(
                        text = stringResource(session.type.labelResource),
                        style = YappTypography.caption1Bold,
                        color = YappMainColor.textSecondary,
                    )
                    session.location?.let { location ->
                        YappIconLabel(
                            text = location,
                            iconResource = DesignSystemR.drawable.ic_map_pin,
                        )
                    }
                    YappIconLabel(
                        text = session.timeLabel,
                        iconResource = DesignSystemR.drawable.ic_clock,
                    )
                }
            }
        }
        HorizontalDivider(color = YappMainColor.divider)
    }
}
