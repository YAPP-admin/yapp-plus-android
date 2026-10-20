package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeQuickActions(
    hasAttended: Boolean,
    canReport: Boolean,
    onSessionsClick: () -> Unit,
    onReportClick: () -> Unit,
    onScoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HomeQuickAction(
            title =
                stringResource(
                    if (hasAttended) R.string.home_attendance_score else R.string.home_all_sessions,
                ),
            description = stringResource(R.string.home_check),
            image = R.drawable.home_action_sessions,
            onClick = if (hasAttended) onScoreClick else onSessionsClick,
            modifier = Modifier.weight(1f),
        )
        HomeQuickAction(
            title =
                stringResource(
                    if (hasAttended) R.string.home_all_sessions else R.string.home_report,
                ),
            description =
                stringResource(
                    if (hasAttended) R.string.home_check else R.string.home_report_ahead,
                ),
            image = R.drawable.home_action_report,
            onClick = if (hasAttended) onSessionsClick else onReportClick,
            enabled = hasAttended || canReport,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun HomeQuickAction(
    title: String,
    description: String,
    image: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = YappHomeColor.surface,
    ) {
        Box(Modifier.heightIn(min = 70.dp)) {
            Image(
                painterResource(image),
                null,
                Modifier.align(Alignment.TopEnd).size(width = 101.5.dp, height = 70.dp),
            )
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                YappText(title, YappHomeTypography.cardTitle, YappHomeColor.text)
                YappText(description, YappHomeTypography.label, YappHomeColor.subtle)
            }
        }
    }
}
