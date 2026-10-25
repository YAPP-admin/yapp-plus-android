package com.yapp.plus.feature.main.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappIconLabel
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.schedule.R
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleTypeUiModel
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
                    .padding(vertical = 20.dp)
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
                YappTypography.scaleT4Bold,
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
                        YappTypography.scaleT4Bold,
                        YappMainColor.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (session.type == ScheduleTypeUiModel.Regular) {
                        Surface(
                            color = YappMainColor.warningWeak,
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            YappText(
                                text = stringResource(session.type.labelResource),
                                style = YappTypography.scaleT2Bold,
                                color = YappMainColor.warning,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    YappIconLabel(
                        text = session.location ?: stringResource(R.string.schedule_location_missing),
                        iconResource = DesignSystemR.drawable.ic_map_pin,
                    )
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
