package com.yapp.plus.feature.signup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.model.AuthStatus
import com.yapp.plus.feature.signup.model.SignUpStep

@Composable
fun SignUpRoute(
    onBack: () -> Unit,
    onSignUpComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler {
        if (uiState.step == SignUpStep.Name) {
            onBack()
        } else {
            viewModel.goBack()
        }
    }

    when (uiState.step) {
        SignUpStep.Name,
        SignUpStep.Phone,
        -> {
            val name = Name(uiState.name)
            val phoneNumber = PhoneNumber(uiState.phoneNumber)
            val isNameStep = uiState.step == SignUpStep.Name
            SignUpContent(
                step = uiState.step,
                value = if (isNameStep) uiState.name else uiState.phoneNumber,
                onValueChange =
                    if (isNameStep) {
                        viewModel::updateName
                    } else {
                        viewModel::updatePhoneNumber
                    },
                isNextEnabled =
                    if (isNameStep) {
                        name.isProvided
                    } else {
                        name.isProvided && phoneNumber.isProvided
                    },
                onNext = viewModel::goToNextStep,
                onBack =
                    if (isNameStep) {
                        onBack
                    } else {
                        viewModel::goBack
                    },
                modifier = modifier,
            )
        }

        SignUpStep.Complete -> {
            AuthStatusContent(
                status = AuthStatus.Complete,
                onBack = viewModel::goBack,
                onAction = {
                    viewModel.clearSignUpInputs()
                    onSignUpComplete()
                },
                modifier = modifier,
            )
        }
    }
}
