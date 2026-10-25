package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeQuickActions(
    onSessionsClick: () -> Unit,
    onScoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HomeQuickAction(
            title = stringResource(R.string.home_all_sessions),
            description = stringResource(R.string.home_check),
            image = R.drawable.home_action_sessions,
            onClick = onSessionsClick,
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
    imageModifier: Modifier = Modifier.size(width = 101.5.dp, height = 70.dp),
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
                Modifier.align(Alignment.TopEnd).then(imageModifier),
                contentScale = ContentScale.Inside,
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
