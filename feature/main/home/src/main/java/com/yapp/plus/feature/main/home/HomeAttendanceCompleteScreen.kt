package com.yapp.plus.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.components.HomeCompletionGraphic
import com.yapp.plus.feature.main.home.components.HomeTopBar

@Composable
fun HomeAttendanceCompleteScreen(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.surface)) {
        HomeTopBar(title = "", onBack = onConfirm)
        BoxWithConstraints(Modifier.weight(1f)) {
            val availableHeight = maxHeight
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = availableHeight)
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
            ) {
                YappText(
                    stringResource(R.string.home_code_complete),
                    YappHomeTypography.completion,
                    YappHomeColor.text,
                )
                Spacer(Modifier.height(10.dp))
                YappText(
                    stringResource(R.string.home_code_complete_description),
                    YappHomeTypography.bodyLarge,
                    YappHomeColor.muted,
                )
                Box(Modifier.fillMaxWidth().weight(1f).heightIn(min = 260.dp), Alignment.Center) {
                    HomeCompletionGraphic()
                }
            }
        }
        YappButton(
            text = stringResource(R.string.home_confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
        )
    }
}
