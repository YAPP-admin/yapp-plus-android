package com.yapp.plus.feature.main.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun MyScreen(
    state: MyProfileUiState = MyProfileUiState.Empty,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize().background(YappMainColor.background),
    ) {
        YappMainHeader(
            title = stringResource(R.string.my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.my_settings),
            onIconClick = onSettingsClick,
        )
        when (state) {
            MyProfileUiState.Loading ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = YappMainColor.brand)
                }

            MyProfileUiState.Empty -> MyProfileMessage(stringResource(R.string.my_profile_empty))
            MyProfileUiState.Error ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    MyProfileMessage(
                        message = stringResource(R.string.my_profile_error),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    YappButton(
                        text = stringResource(R.string.my_retry),
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        variant = YappButtonVariant.SolidNeutral,
                        size = YappButtonSize.HomeSecondary,
                    )
                }

            is MyProfileUiState.Content -> MyProfileContent(state.profile)
        }
    }
}

@Composable
private fun MyProfileContent(profile: MyProfileUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = YappMainColor.surface,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                YappText(
                    text = stringResource(R.string.my_information_title),
                    style = YappTypography.headline2Bold,
                    color = YappMainColor.textPrimary,
                )
                ProfileField(
                    label = stringResource(R.string.my_name_label),
                    value = profile.name,
                )
                Spacer(Modifier.fillMaxWidth().height(1.dp).background(YappMainColor.divider))
                ProfileField(
                    label = stringResource(R.string.my_phone_number_label),
                    value = profile.phoneNumber,
                )
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            text = label,
            style = YappTypography.body2NormalRegular,
            color = YappMainColor.textSecondary,
        )
        YappText(
            text = value,
            style = YappTypography.body1NormalMedium,
            color = YappMainColor.textPrimary,
        )
    }
}

@Composable
private fun MyProfileMessage(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.graphic_none),
            contentDescription = null,
            modifier = Modifier.size(width = 120.dp, height = 74.dp),
        )
        Spacer(Modifier.height(20.dp))
        YappText(
            text = message,
            style = YappTypography.body1NormalRegular,
            color = YappMainColor.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
