package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().heightIn(min = 54.dp)) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
        ) {
            Icon(
                painterResource(DesignSystemR.drawable.ic_chevron_left),
                stringResource(R.string.home_back),
                Modifier.size(24.dp),
                YappHomeColor.muted,
            )
        }
        YappText(
            title,
            YappHomeTypography.cardTitle,
            YappHomeColor.text,
            Modifier.align(Alignment.Center).padding(horizontal = 56.dp),
        )
    }
}
