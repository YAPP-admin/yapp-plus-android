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
import com.yapp.plus.core.designsystem.components.auth.AuthGraphic
import com.yapp.plus.core.designsystem.components.auth.AuthHeading
import com.yapp.plus.core.designsystem.components.auth.AuthLayout
import com.yapp.plus.core.designsystem.components.auth.AuthPrimaryAction
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.signup.model.AuthStatus

@Composable
fun AuthStatusContent(
    status: AuthStatus,
    onBack: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    isActionEnabled: Boolean = true,
) {
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
                AuthHeading(stringResource(status.titleResource))
                Spacer(Modifier.height(10.dp))
                YappText(
                    text = stringResource(status.descriptionResource),
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
                        AuthGraphic(resource = status.graphicResource)
                    }
                } else {
                    Spacer(Modifier.height(160.dp))
                    AuthGraphic(
                        resource = status.graphicResource,
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
        AuthStatus.Pending -> Unit
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
