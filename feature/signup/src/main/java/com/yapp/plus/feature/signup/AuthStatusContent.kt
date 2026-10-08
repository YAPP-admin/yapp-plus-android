package com.yapp.plus.feature.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.component.auth.AuthGraphic
import com.yapp.plus.core.designsystem.component.auth.AuthHeading
import com.yapp.plus.core.designsystem.component.auth.AuthLayout
import com.yapp.plus.core.designsystem.component.auth.AuthPrimaryAction
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun AuthStatusContent(
    status: AuthStatus,
    onBack: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    isActionEnabled: Boolean = true,
) {
    val title =
        when (status) {
            AuthStatus.Pending -> R.string.auth_pending_title
            AuthStatus.Complete -> R.string.auth_complete_title
            AuthStatus.Problem -> R.string.auth_problem_title
        }
    val description =
        when (status) {
            AuthStatus.Pending -> R.string.auth_pending_description
            AuthStatus.Complete -> R.string.auth_complete_description
            AuthStatus.Problem -> R.string.auth_problem_description
        }
    val graphic =
        when (status) {
            AuthStatus.Pending -> DesignSystemR.drawable.graphic_point_construction
            AuthStatus.Complete -> DesignSystemR.drawable.graphic_point_happy
            AuthStatus.Problem -> DesignSystemR.drawable.graphic_point_disabled
        }
    AuthLayout(
        onBack = onBack,
        modifier = modifier,
        bottom = { AuthStatusAction(status, onAction, isActionEnabled) },
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 24.dp),
            ) {
                AuthHeading(stringResource(title))
                Spacer(Modifier.height(10.dp))
                YappText(
                    text = stringResource(description),
                    style = YappTypography.body1NormalRegular.copy(letterSpacing = 0.sp),
                    color = YappAuthColor.textSecondary,
                )
                if (status == AuthStatus.Pending) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        AuthGraphic(resource = graphic)
                    }
                } else {
                    Spacer(Modifier.height(160.dp))
                    AuthGraphic(
                        resource = graphic,
                        height = if (status == AuthStatus.Problem) 170.dp else 180.dp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AuthStatusAction(
    status: AuthStatus,
    onAction: () -> Unit,
    isEnabled: Boolean,
) {
    when (status) {
        AuthStatus.Pending ->
            AuthPrimaryAction(
                text = stringResource(R.string.auth_pending_demo_action),
                onClick = onAction,
                isEnabled = isEnabled,
            )
        AuthStatus.Complete ->
            AuthPrimaryAction(stringResource(R.string.auth_start), onAction, isEnabled)
        AuthStatus.Problem ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(onClick = onAction, enabled = isEnabled) {
                    YappText(
                        text = stringResource(R.string.auth_contact),
                        style =
                            YappTypography.headline1Bold.copy(
                                lineHeight = 24.sp,
                                letterSpacing = 0.sp,
                            ),
                        color = YappAuthColor.textSubtle,
                    )
                }
            }
    }
}
