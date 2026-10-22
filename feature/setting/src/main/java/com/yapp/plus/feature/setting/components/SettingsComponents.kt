package com.yapp.plus.feature.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.setting.R
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun SettingsHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(54.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp).size(48.dp),
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_chevron_left),
                contentDescription = stringResource(R.string.settings_back),
                modifier = Modifier.size(24.dp),
                tint = YappMainColor.textPrimary,
            )
        }
        YappText(
            text = stringResource(R.string.settings_title),
            style = YappTypography.headline1Bold,
            color = YappMainColor.textPrimary,
        )
    }
}

@Composable
fun SettingsNotificationRow(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .toggleable(value = enabled, role = Role.Switch, onValueChange = onEnabledChange)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            text = stringResource(R.string.settings_notifications),
            style = YappTypography.body1NormalMedium,
            color = YappMainColor.textPrimary,
        )
        Switch(
            checked = enabled,
            onCheckedChange = null,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = YappMainColor.surface,
                    checkedTrackColor = YappMainColor.brand,
                    checkedBorderColor = YappMainColor.brand,
                    uncheckedThumbColor = YappMainColor.surface,
                    uncheckedTrackColor = YappMainColor.textFaint,
                    uncheckedBorderColor = YappMainColor.textFaint,
                ),
        )
    }
}

@Composable
fun SettingsLinkRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            text = title,
            style = YappTypography.body1NormalMedium,
            color = YappMainColor.textPrimary,
        )
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = YappMainColor.textSecondary,
        )
    }
}

@Composable
fun SettingsDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(1.dp)
                .background(YappMainColor.divider),
    )
}
