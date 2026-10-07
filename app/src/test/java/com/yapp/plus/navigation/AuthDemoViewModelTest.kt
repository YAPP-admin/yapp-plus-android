package com.yapp.plus.navigation

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthDemoViewModelTest {
    @Test
    fun updatesOneInputWithoutClearingTheOther() {
        val viewModel = AuthDemoViewModel(SavedStateHandle())

        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")
        viewModel.updateName("수정한 이름")

        assertEquals(
            AuthDemoUiState(name = "수정한 이름", phoneNumber = "01012345678"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun recreatesInputsFromTheSavedStateHandle() {
        val handle = SavedStateHandle()
        val viewModel = AuthDemoViewModel(handle)
        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")

        val restoredHandle =
            SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
        val restoredViewModel = AuthDemoViewModel(restoredHandle)

        assertEquals(viewModel.uiState.value, restoredViewModel.uiState.value)
        restoredViewModel.updatePhoneNumber("")
        assertEquals("이름", restoredViewModel.uiState.value.name)
        assertEquals("", restoredHandle.get<String>(AuthDemoViewModel.PHONE_NUMBER_KEY))
    }

    @Test
    fun preservesUnvalidatedInputForTheDemo() {
        val viewModel = AuthDemoViewModel(SavedStateHandle())

        viewModel.updateName(" ")
        viewModel.updatePhoneNumber("번호 형식 미확정")

        assertEquals(
            AuthDemoUiState(name = " ", phoneNumber = "번호 형식 미확정"),
            viewModel.uiState.value,
        )
    }
}
