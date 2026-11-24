package com.yapp.plus.feature.signup.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.popup.YappAlertDialog
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.feature.signup.R

@Composable
internal fun SignUpErrorDialog(
    failure: AuthFailure,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
) {
    val requiresNewLogin =
        failure == AuthFailure.AuthenticationRejected || failure == AuthFailure.UserAlreadyExists
    val bodyResource =
        when (failure) {
            AuthFailure.AuthenticationRejected -> R.string.auth_signup_expired
            AuthFailure.UserAlreadyExists -> R.string.auth_signup_already_exists
            AuthFailure.InvalidRequest -> R.string.auth_signup_invalid_request
            AuthFailure.Unavailable -> R.string.auth_signup_error_description
        }
    YappAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.auth_signup_error_title),
        body = stringResource(bodyResource),
        confirmText =
            stringResource(
                if (requiresNewLogin) R.string.auth_signup_login_again else R.string.auth_close,
            ),
        dismissText = if (requiresNewLogin) stringResource(R.string.auth_close) else null,
        onConfirm = if (requiresNewLogin) onBack else onDismiss,
        onDismiss = if (requiresNewLogin) onBack else onDismiss,
    )
}
