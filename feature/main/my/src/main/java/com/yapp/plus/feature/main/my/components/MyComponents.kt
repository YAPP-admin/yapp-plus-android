package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.chip.YappChip
import com.yapp.plus.core.designsystem.components.chip.YappChipColor
import com.yapp.plus.core.designsystem.components.chip.YappChipSize
import com.yapp.plus.core.designsystem.components.chip.YappChipStyle
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun MyMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(title, YappTypography.body1NormalBold, YappMainColor.textPrimary)
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = YappMainColor.textFaint,
        )
    }
}

@Composable
fun MyProfileCard(
    name: String,
    role: String,
    generation: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.graphic_user_profile),
            contentDescription = null,
            modifier = Modifier.size(66.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(name, YappTypography.heading2Bold, YappMainColor.textPrimary)
                YappChip(
                    text = role,
                    color = YappChipColor.Orange,
                    size = YappChipSize.Large,
                    style = YappChipStyle.Fill,
                )
            }
            YappText(generation, YappTypography.label1NormalMedium, YappMainColor.textSecondary)
        }
    }
}

@Composable
fun MySummaryCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = YappMainColor.surface,
        border = BorderStroke(1.dp, YappMainColor.divider),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(YappMainColor.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    stringResource(R.string.my_summary_title),
                    YappTypography.label1NormalBold,
                    YappMainColor.textPrimary,
                )
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_help),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified,
                )
                Spacer(Modifier.weight(1f))
                YappText(
                    stringResource(R.string.my_total_score),
                    YappTypography.label2Medium,
                    YappMainColor.textSecondary,
                )
                YappText(
                    stringResource(R.string.my_score),
                    YappTypography.body1NormalBold,
                    YappMainColor.brand,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
            ) {
                MyStatistic(
                    label = stringResource(R.string.my_present),
                    count = 3,
                    modifier = Modifier.weight(1f),
                )
                MyStatistic(
                    label = stringResource(R.string.my_late),
                    count = 1,
                    modifier = Modifier.weight(1f),
                )
                MyStatistic(
                    label = stringResource(R.string.my_absent),
                    count = 0,
                    modifier = Modifier.weight(1f),
                )
                MyStatistic(
                    label = stringResource(R.string.my_exemption),
                    count = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun MyStatistic(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        YappText(label, YappTypography.caption1Bold, YappMainColor.textSecondary)
        YappText(count.toString(), YappTypography.label1NormalBold, YappMainColor.textPrimary)
    }
}
