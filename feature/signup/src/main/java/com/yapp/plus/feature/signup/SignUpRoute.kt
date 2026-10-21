package com.yapp.plus.feature.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

@Composable
fun SignUpNameRoute(
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val name = Name(uiState.name)
    SignUpContent(
        step = SignUpStep.Name,
        value = uiState.name,
        onValueChange = viewModel::updateName,
        isNextEnabled = name.isProvided,
        onNext = {
            if (name.isProvided) {
                onNext()
            }
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun SignUpPhoneRoute(
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val name = Name(uiState.name)
    val phoneNumber = PhoneNumber(uiState.phoneNumber)
    SignUpContent(
        step = SignUpStep.Phone,
        value = uiState.phoneNumber,
        onValueChange = viewModel::updatePhoneNumber,
        isNextEnabled = name.isProvided && phoneNumber.isProvided,
        onNext = {
            if (name.isProvided && phoneNumber.isProvided) {
                onNext()
            }
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun SignUpCompleteRoute(
    onBack: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    AuthStatusContent(
        status = AuthStatus.Complete,
        onBack = onBack,
        onAction = {
            viewModel.clearSignUpInputs()
            onAction()
        },
        modifier = modifier,
    )
}
