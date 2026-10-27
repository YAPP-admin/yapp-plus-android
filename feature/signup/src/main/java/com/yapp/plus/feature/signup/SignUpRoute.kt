package com.yapp.plus.feature.signup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.input.YappInputState
import com.yapp.plus.domain.auth.RegistrationFieldError
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.components.SignUpErrorDialog
import com.yapp.plus.feature.signup.model.AuthStatus
import com.yapp.plus.feature.signup.model.SignUpStep
import kotlinx.coroutines.launch

@Composable
fun SignUpRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SignUpRoute(
        onBack = onBack,
        registrationToken = null,
        onRegistrationCompleted = {},
        modifier = modifier,
    )
}

@Composable
fun SignUpRoute(
    onBack: () -> Unit,
    registrationToken: String?,
    onRegistrationCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isNameStep = uiState.step == SignUpStep.Name
    val isSubmissionStep = !isNameStep || uiState.canSubmitFromName
    val inputError = if (isNameStep) uiState.nameError else uiState.phoneNumberError
    val canSubmit =
        registrationToken?.isNotBlank() == true &&
            PhoneNumber(uiState.phoneNumber).toE164OrNull() != null &&
            uiState.phoneNumberError == null
    val navigateBack: () -> Unit = {
        when (uiState.step) {
            SignUpStep.Name -> onBack()
            SignUpStep.Phone -> viewModel.goBack()
            SignUpStep.Complete -> onRegistrationCompleted()
        }
    }
    BackHandler(enabled = !uiState.isSubmitting, onBack = navigateBack)
    if (uiState.step == SignUpStep.Complete) {
        AuthStatusContent(
            status = AuthStatus.Complete,
            onBack = onRegistrationCompleted,
            onAction = onRegistrationCompleted,
            modifier = modifier,
        )
    } else {
        val coroutineScope = rememberCoroutineScope()
        SignUpContent(
            step = uiState.step,
            value = if (isNameStep) uiState.name else uiState.phoneNumber,
            onValueChange = if (isNameStep) viewModel::updateName else viewModel::updatePhoneNumber,
            isNextEnabled =
                Name(uiState.name).isProvided &&
                    uiState.nameError == null &&
                    !uiState.isSubmitting &&
                    (!isSubmissionStep || canSubmit),
            onNext = {
                if (isSubmissionStep) {
                    coroutineScope.launch { viewModel.submit(registrationToken) }
                } else {
                    viewModel.goToNextStep()
                }
            },
            onBack = { if (!uiState.isSubmitting) navigateBack() },
            modifier = modifier,
            isInputEnabled = !uiState.isSubmitting,
            inputState = if (inputError != null) YappInputState.Error else YappInputState.Default,
            supportingText =
                inputError?.let { reason ->
                    stringResource(
                        if (isNameStep) {
                            nameErrorMessage(reason)
                        } else {
                            phoneNumberErrorMessage(reason)
                        },
                    )
                },
            actionText =
                stringResource(
                    when {
                        uiState.isSubmitting -> R.string.auth_signup_submitting
                        isSubmissionStep -> R.string.auth_signup_submit
                        else -> R.string.auth_next
                    },
                ),
        )
        uiState.failure?.let { failure ->
            SignUpErrorDialog(
                failure = failure,
                onDismiss = viewModel::dismissFailure,
                onRetry = {
                    viewModel.dismissFailure()
                    coroutineScope.launch { viewModel.submit(registrationToken) }
                },
                onBack = onBack,
            )
        }
    }
}

private fun nameErrorMessage(reason: RegistrationFieldError.Reason): Int =
    when (reason) {
        RegistrationFieldError.Reason.Required -> R.string.auth_name_required
        RegistrationFieldError.Reason.TooLong -> R.string.auth_name_too_long
        else -> R.string.auth_name_invalid
    }

private fun phoneNumberErrorMessage(reason: RegistrationFieldError.Reason): Int =
    when (reason) {
        RegistrationFieldError.Reason.Required -> R.string.auth_phone_required
        RegistrationFieldError.Reason.AlreadyExists -> R.string.auth_phone_already_exists
        else -> R.string.auth_phone_invalid
    }
