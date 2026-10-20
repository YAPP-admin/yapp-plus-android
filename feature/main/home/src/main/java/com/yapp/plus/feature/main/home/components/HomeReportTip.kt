package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeReportTip(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-3).dp),
    ) {
        val color = YappHomeColor.muted
        Image(painterResource(R.drawable.home_tooltip_arrow), null, Modifier.size(10.dp))
        Surface(color = color, shape = RoundedCornerShape(8.dp)) {
            YappText(
                stringResource(R.string.home_report_tip),
                YappHomeTypography.caption,
                YappHomeColor.surface,
                Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
