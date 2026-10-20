package com.yapp.plus.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.auth.AuthGraphic
import com.yapp.plus.core.designsystem.components.auth.AuthHeading
import com.yapp.plus.core.designsystem.components.auth.AuthLayout
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun LoginContent(
    onBack: () -> Unit,
    onGoogleLogin: () -> Unit,
    modifier: Modifier = Modifier,
    isLoginEnabled: Boolean = true,
) {
    AuthLayout(onBack = onBack, modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 24.dp),
        ) {
            AuthHeading(stringResource(R.string.auth_login_title))
            Spacer(Modifier.height(48.dp))
            AuthGraphic(
                resource = DesignSystemR.drawable.graphic_point_enabled,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(64.dp))
            YappButton(
                text = stringResource(R.string.auth_google_continue),
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                variant = YappButtonVariant.OutlinedAssistive,
                size = YappButtonSize.Social,
                enabled = isLoginEnabled,
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_auth_google),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
    }
}
