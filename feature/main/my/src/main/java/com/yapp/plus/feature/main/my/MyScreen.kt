package com.yapp.plus.feature.main.my

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.components.MyAttendanceHistory
import com.yapp.plus.feature.main.my.components.MyMenuRow
import com.yapp.plus.feature.main.my.components.MyProfileSummary
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun MyScreen(
    state: MyUiState = MyUiState(),
    modifier: Modifier = Modifier,
    onRetrySubmittedProfile: () -> Unit = {},
    onRetryTestProfile: () -> Unit = {},
    onAttendanceHistoryClick: () -> Unit = {},
    onRetryAttendanceHistory: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize().background(YappMainColor.surface),
    ) {
        YappMainHeader(
            title = stringResource(R.string.my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.my_settings),
            onIconClick = onSettingsClick,
            iconTint = YappMainColor.textSubtle,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                MyProfileSummary(
                    state = state.testProfile,
                    onRetry = onRetryTestProfile,
                )
            }
            item {
                MyProfileContent(
                    state = state.submittedProfile,
                    onRetry = onRetrySubmittedProfile,
                )
            }
            item {
                Spacer(Modifier.height(20.dp))
                Spacer(Modifier.fillMaxWidth().height(8.dp).background(YappMainColor.background))
                Spacer(Modifier.height(12.dp))
                MyMenuRow(
                    title = stringResource(R.string.my_attendance_history_title),
                    expanded = state.isAttendanceHistoryExpanded,
                    onClick = onAttendanceHistoryClick,
                )
            }
            if (state.isAttendanceHistoryExpanded) {
                item {
                    MyAttendanceHistory(
                        state = state.attendanceHistory,
                        onRetry = onRetryAttendanceHistory,
                    )
                }
            }
        }
    }
}

@Composable
internal fun MyProfileContent(
    state: MyProfileUiState,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        color = YappMainColor.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, YappMainColor.divider),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            YappText(
                text = stringResource(R.string.my_information_title),
                style = YappTypography.scaleT4Bold,
                color = YappMainColor.textPrimary,
            )
            when (state) {
                MyProfileUiState.Unavailable ->
                    ProfileStatus(stringResource(R.string.my_profile_preparing))
                MyProfileUiState.Loading ->
                    ProfileStatus(
                        message = stringResource(R.string.my_profile_loading),
                        showLoadingIndicator = true,
                    )
                MyProfileUiState.Empty -> ProfileStatus(stringResource(R.string.my_profile_empty))
                MyProfileUiState.Error -> ProfileError(onRetry)
                is MyProfileUiState.Content -> MyProfileDetails(state.profile)
            }
        }
    }
}

@Composable
private fun MyProfileDetails(profile: MyProfileUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

@Composable
private fun ProfileError(onRetry: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            text = stringResource(R.string.my_profile_error),
            style = YappTypography.body2NormalRegular,
            color = YappMainColor.textSecondary,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRetry) {
            YappText(
                text = stringResource(R.string.my_retry),
                style = YappTypography.label1NormalMedium,
                color = YappMainColor.brand,
            )
        }
    }
}

@Composable
private fun ProfileStatus(
    message: String,
    showLoadingIndicator: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showLoadingIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = YappMainColor.brand,
                strokeWidth = 2.dp,
            )
        }
        YappText(
            text = message,
            style = YappTypography.body2NormalRegular,
            color = YappMainColor.textSecondary,
        )
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
            style = YappTypography.scaleT5Regular,
            color = YappMainColor.textPrimary,
        )
    }
}
