package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
internal fun MyStatsCard(
    title: String,
    valueLabel: String?,
    value: String?,
    entries: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    titleAction: @Composable () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappMainColor.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, YappMainColor.divider),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 38.dp)
                        .background(YappMainColor.basement)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    YappText(title, YappTypography.scaleT4Bold, YappMainColor.textPrimary)
                    titleAction()
                }
                valueLabel?.let {
                    YappText(it, YappTypography.scaleT3Medium, YappMainColor.textSecondary)
                }
                value?.let {
                    YappText(it, YappTypography.scaleT5Bold, YappMainColor.brand)
                }
            }
            Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                entries.forEach { (label, count) ->
                    MyStatValue(label, count, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun MyStatValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        YappText(label, YappTypography.scaleT2Bold, YappMainColor.textSubtle)
        YappText(value, YappTypography.scaleT4Bold, YappMainColor.textPrimary)
    }
}
