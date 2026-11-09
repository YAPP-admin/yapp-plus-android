package com.yapp.plus.feature.setting.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.setting.R

@Composable
internal fun SettingsNotificationRetryRow(
    onRetry: () -> Unit,
    isRetrying: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            text = stringResource(R.string.settings_notifications),
            style = YappTypography.scaleT5Bold,
            color = YappMainColor.textPrimary,
        )
        Box(Modifier.size(width = 52.dp, height = 48.dp), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = onRetry,
                enabled = !isRetrying,
                modifier = Modifier.size(48.dp).border(1.dp, YappMainColor.border, CircleShape),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_restart_alt),
                    contentDescription = stringResource(R.string.settings_notifications_reload),
                    tint = if (isRetrying) YappMainColor.textFaint else YappMainColor.textSecondary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
