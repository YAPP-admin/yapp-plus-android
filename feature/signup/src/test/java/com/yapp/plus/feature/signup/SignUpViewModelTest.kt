package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.feature.signup.model.SignUpStep
import com.yapp.plus.feature.signup.model.SignUpUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class SignUpViewModelTest {
    @Test
    fun updatesOneInputWithoutClearingTheOther() {
        val viewModel = SignUpViewModel(SavedStateHandle())

        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")
        viewModel.updateName("수정한 이름")

        assertEquals(
            SignUpUiState(name = "수정한 이름", phoneNumber = "01012345678"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun recreatesInputsFromTheSavedStateHandle() {
        val handle = SavedStateHandle()
        val viewModel = SignUpViewModel(handle)
        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")

        val restoredHandle =
            SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
        val restoredViewModel = SignUpViewModel(restoredHandle)

        assertEquals(viewModel.uiState.value, restoredViewModel.uiState.value)
        restoredViewModel.updatePhoneNumber("")
        assertEquals("이름", restoredViewModel.uiState.value.name)
        assertEquals("", restoredHandle.get<String>(SignUpViewModel.PHONE_NUMBER_KEY))
    }

    @Test
    fun preservesInputWithoutPhoneFormatValidation() {
        val viewModel = SignUpViewModel(SavedStateHandle())

        viewModel.updateName(" ")
        viewModel.updatePhoneNumber("번호 형식 미확정")

        assertEquals(
            SignUpUiState(name = " ", phoneNumber = "번호 형식 미확정"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun migratesPreviouslySavedInputs() {
        val viewModel =
            SignUpViewModel(
                SavedStateHandle(
                    mapOf(
                        "authDemoName" to "이름",
                        "authDemoPhoneNumber" to "번호 형식 미확정",
                    ),
                ),
            )

        assertEquals(
            SignUpUiState(name = "이름", phoneNumber = "번호 형식 미확정"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun validInputsDoNotCompleteRegistrationWithoutServerAuthentication() {
        val viewModel = SignUpViewModel(SavedStateHandle())
        viewModel.updateName("홍길동")
        viewModel.goToNextStep()
        viewModel.updatePhoneNumber("01012345678")
        repeat(3) { viewModel.goToNextStep() }
        assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
        assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun previouslySavedCompletionRestoresToPhoneInput() {
        val viewModel =
            SignUpViewModel(
                SavedStateHandle(mapOf(SignUpViewModel.STEP_KEY to SignUpStep.Complete.name)),
            )
        assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
    }

    @Test
    fun blankNameCannotAdvance() {
        val viewModel = SignUpViewModel(SavedStateHandle())
        viewModel.updateName("  ")
        viewModel.goToNextStep()
        assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
    }
}
