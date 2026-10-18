package com.yapp.plus.feature.main.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.main.YappDemoDataLabel
import com.yapp.plus.core.designsystem.component.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.my.component.MyMenuItem
import com.yapp.plus.feature.main.my.component.MyProfileCard
import com.yapp.plus.feature.main.my.component.MySummaryCard
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        YappMainHeader(
            title = stringResource(R.string.my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.my_settings),
            onIconClick = onSettingsClick,
        )
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
            ) {
                MyProfileCard(
                    name = stringResource(R.string.my_name),
                    role = stringResource(R.string.my_role),
                    generation = stringResource(R.string.my_generation),
                )
                MySummaryCard()
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(YappMainColor.background),
            )
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp),
            ) {
                MyMenuItem(
                    title = stringResource(R.string.my_history),
                    onClick = onHistoryClick,
                )
                MyMenuItem(
                    title = stringResource(R.string.my_support),
                    onClick = onSupportClick,
                )
                YappDemoDataLabel(
                    text = stringResource(R.string.my_demo_data),
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                )
            }
        }
    }
}
