package com.yapp.plus.feature.signup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.feature.signup.model.SignUpStep

@Composable
fun SignUpRoute(
    onBack: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onSignUpComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isNameStep = uiState.step == SignUpStep.Name
    val navigateBack: () -> Unit = {
        if (isNameStep) onBack() else viewModel.goBack()
    }
    BackHandler(onBack = navigateBack)
    SignUpContent(
        step = uiState.step,
        value = if (isNameStep) uiState.name else uiState.phoneNumber,
        onValueChange = if (isNameStep) viewModel::updateName else viewModel::updatePhoneNumber,
        isNextEnabled = isNameStep && Name(uiState.name).isProvided,
        onNext = viewModel::goToNextStep,
        onBack = navigateBack,
        modifier = modifier,
    )
}
