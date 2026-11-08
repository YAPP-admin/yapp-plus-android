package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState.Approved
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState.ConfirmedAbsent
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState.Rejected
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState.UploadFailed

@Composable
internal fun HomeTeamPhotoResultSheet(
    state: HomeTeamPhotoResultSheetState,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onContactStaff: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title =
        when (state) {
            Approved -> R.string.home_team_result_approved_title
            is Rejected -> R.string.home_team_result_rejected_title
            is ConfirmedAbsent -> R.string.home_team_result_absent_title
            UploadFailed -> R.string.home_team_result_upload_failed_title
        }
    val description =
        when (state) {
            Approved -> stringResource(R.string.home_team_result_approved_description)
            is Rejected ->
                state.retryDeadlineLabel?.takeIf { it.isNotBlank() }?.let {
                    stringResource(R.string.home_team_result_rejected_deadline, it)
                } ?: stringResource(R.string.home_team_result_rejected_description)
                    .takeIf { state.canRetry }
            is ConfirmedAbsent -> stringResource(R.string.home_team_result_absent_description)
            UploadFailed -> stringResource(R.string.home_team_result_upload_failed_description)
        }
    val reason =
        when (state) {
            is Rejected -> state.reason
            is ConfirmedAbsent -> state.reason
            else -> null
        }
    val isRetry = state is Rejected || state == UploadFailed
    YappBottomSheet(
        onDismissRequest = onDismiss,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        showHandle = true,
        title = null,
        body = null,
        confirmText = null,
        dismissText = null,
        showActions = false,
        variant = YappBottomSheetVariant.Home,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                YappText(
                    text = stringResource(title),
                    style = YappTypography.scaleT7Bold,
                    color = YappHomeColor.text,
                    modifier = Modifier.semantics { heading() },
                )
                description?.let {
                    YappText(it, YappTypography.scaleT4Medium, YappHomeColor.muted)
                }
            }
            when {
                state == Approved -> HomeCompletionGraphic(Modifier.widthIn(max = 315.dp))
                state == UploadFailed ->
                    HomeTeamPhotoResultNotice(stringResource(R.string.home_team_result_size_notice))
                !reason.isNullOrBlank() ->
                    HomeTeamPhotoResultNotice(
                        stringResource(R.string.home_team_result_reason, reason),
                    )
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (state is ConfirmedAbsent) {
                    Surface(
                        onClick = onContactStaff,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = YappHomeColor.background,
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            YappText(
                                text = stringResource(R.string.home_team_result_contact_staff),
                                style = YappTypography.scaleT5Bold,
                                color = YappHomeColor.subtle,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                YappButton(
                    text =
                        stringResource(
                            if (isRetry) R.string.home_team_result_retry else R.string.home_confirm,
                        ),
                    onClick = if (isRetry) onRetry else onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    variant = YappButtonVariant.SolidBrand,
                    size = YappButtonSize.HomePrimary,
                    enabled = state !is Rejected || state.canRetry,
                )
            }
        }
    }
}

@Composable
internal fun HomeTeamPhotoResultNotice(text: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.criticalWeak, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_home_team_result_notice),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        YappText(text, YappTypography.scaleT4Medium, YappMainColor.critical)
    }
}
