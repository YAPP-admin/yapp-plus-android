package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.chip.YappChip
import com.yapp.plus.core.designsystem.components.chip.YappChipColor
import com.yapp.plus.core.designsystem.components.chip.YappChipSize
import com.yapp.plus.core.designsystem.components.chip.YappChipStyle
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.feature.main.my.R
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState

@Composable
internal fun MyProfileSection(
    state: MyProfileUiState,
    onRetry: () -> Unit,
    showRequestState: Boolean = true,
) {
    val savedName =
        when (state) {
            is MyProfileUiState.Loading -> state.savedName
            is MyProfileUiState.Error -> state.savedName
            MyProfileUiState.Unavailable, is MyProfileUiState.Content -> null
        }
    val profile =
        (state as? MyProfileUiState.Content)?.profile
            ?: savedName?.takeIf(String::isNotBlank)?.let { MyProfileUiModel(name = it) }
    Column {
        MyProfile(profile)
        if (!showRequestState) return@Column
        when (state) {
            is MyProfileUiState.Loading ->
                MyRequestState(
                    message = stringResource(R.string.my_profile_loading),
                    isLoading = true,
                )
            is MyProfileUiState.Error ->
                MyRequestState(
                    message = stringResource(R.string.my_profile_error),
                    onRetry = onRetry,
                )
            MyProfileUiState.Unavailable, is MyProfileUiState.Content -> Unit
        }
    }
}

@Composable
internal fun MyProfile(
    profile: MyProfileUiModel?,
    modifier: Modifier = Modifier,
) {
    val emptyValue = stringResource(R.string.my_empty_value)
    Row(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.graphic_user_profile),
            contentDescription = null,
            modifier = Modifier.size(66.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    text = profile?.name ?: emptyValue,
                    style = YappTypography.scaleT7Bold,
                    color = YappMainColor.textPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                )
                YappChip(
                    text = stringResource(profile?.role.labelResource()),
                    color = YappChipColor.Brand,
                    size = YappChipSize.Profile,
                    style = YappChipStyle.Fill,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    profile?.generation?.let { stringResource(R.string.my_profile_generation, it) }
                        ?: emptyValue,
                    YappTypography.scaleT4Medium,
                    YappMainColor.textSecondary,
                )
                Spacer(
                    Modifier.width(1.dp).height(10.dp).background(YappMainColor.textSecondary),
                )
                YappText(
                    stringResource(profile?.position.labelResource()),
                    YappTypography.scaleT4Medium,
                    YappMainColor.textSecondary,
                )
            }
        }
    }
}

private fun MembershipStatus?.labelResource(): Int =
    when (this) {
        MembershipStatus.PLAYER -> R.string.my_profile_role_player
        MembershipStatus.OB -> R.string.my_profile_role_ob
        MembershipStatus.COMPLETED_MEMBER -> R.string.my_profile_role_completed
        MembershipStatus.DEFERRED -> R.string.my_profile_role_deferred
        MembershipStatus.UNKNOWN, null -> R.string.my_empty_value
    }

private fun UserPosition?.labelResource(): Int =
    when (this) {
        UserPosition.PM -> R.string.my_profile_position_pm
        UserPosition.DESIGN -> R.string.my_profile_position_design
        UserPosition.WEB -> R.string.my_profile_position_web
        UserPosition.MOBILE -> R.string.my_profile_position_mobile
        UserPosition.ANDROID -> R.string.my_profile_position_android
        UserPosition.IOS -> R.string.my_profile_position_ios
        UserPosition.SERVER -> R.string.my_profile_position_server
        UserPosition.AI_NATIVE -> R.string.my_profile_position_ai_native
        UserPosition.UNKNOWN, null -> R.string.my_empty_value
    }
