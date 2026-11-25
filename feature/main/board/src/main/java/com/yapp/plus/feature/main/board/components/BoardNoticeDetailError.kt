package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.R
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun BoardNoticeDetailError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(160.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            YappText(
                text = stringResource(R.string.board_error_title),
                style = YappTypography.scaleT10Bold,
                color = YappMainColor.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            YappText(
                text = stringResource(R.string.board_error_description),
                style = YappTypography.scaleT5Regular,
                color = YappMainColor.textSecondary,
            )
        }
        Image(
            painter = painterResource(DesignSystemR.drawable.graphic_point_disabled),
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = 350.dp)
                    .fillMaxWidth()
                    .aspectRatio(350f / 170f),
        )
    }
}
