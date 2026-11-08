package com.yapp.plus.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.components.HomeCompletionGraphic
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoReceiptDetails
import com.yapp.plus.feature.main.home.components.HomeTopBar
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoReceiptUiModel

@Composable
internal fun HomeTeamPhotoSubmittedScreen(
    receipt: HomeTeamPhotoReceiptUiModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.surface)) {
        HomeTopBar(title = "", onBack = onBack, modifier = Modifier.height(51.dp))
        BoxWithConstraints(Modifier.weight(1f)) {
            val availableHeight = maxHeight
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = availableHeight)
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    YappText(
                        text = stringResource(R.string.home_team_result_submitted_title),
                        style = YappTypography.scaleT10Bold,
                        color = YappHomeColor.text,
                        modifier = Modifier.semantics { heading() },
                    )
                    YappText(
                        text = stringResource(R.string.home_team_result_submitted_description),
                        style = YappTypography.scaleT5Regular,
                        color = YappHomeColor.muted,
                    )
                }
                HomeCompletionGraphic(Modifier.padding(vertical = 20.dp))
                HomeTeamPhotoReceiptDetails(receipt)
            }
        }
        YappButton(
            text = stringResource(R.string.home_confirm),
            onClick = onConfirm,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
        )
    }
}
