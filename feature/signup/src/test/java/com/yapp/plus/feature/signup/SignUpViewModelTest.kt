package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.model.SignUpStep
import com.yapp.plus.feature.signup.model.SignUpUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun updatesOneInputWithoutClearingTheOther() {
        val viewModel = SignUpViewModel(SavedStateHandle(), FakeProfileRepository())

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
        val viewModel = SignUpViewModel(handle, FakeProfileRepository())
        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")

        val restoredHandle =
            SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
        val restoredViewModel = SignUpViewModel(restoredHandle, FakeProfileRepository())

        assertEquals(viewModel.uiState.value, restoredViewModel.uiState.value)
        restoredViewModel.updatePhoneNumber("")
        assertEquals("이름", restoredViewModel.uiState.value.name)
        assertEquals("", restoredHandle.get<String>(SignUpViewModel.PHONE_NUMBER_KEY))
    }

    @Test
    fun preservesInputWithoutPhoneFormatValidation() {
        val viewModel = SignUpViewModel(SavedStateHandle(), FakeProfileRepository())

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
                FakeProfileRepository(),
            )

        assertEquals(
            SignUpUiState(name = "이름", phoneNumber = "번호 형식 미확정"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun savesProfileBeforeShowingCompletion() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeProfileRepository()
            val viewModel = SignUpViewModel(SavedStateHandle(), repository)
            viewModel.updateName("홍길동")
            viewModel.goToNextStep()
            viewModel.updatePhoneNumber("01012345678")

            viewModel.goToNextStep()
            advanceUntilIdle()

            assertEquals(Profile(Name("홍길동"), PhoneNumber("01012345678")), repository.savedProfile)
            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
        }

    @Test
    fun failedSaveKeepsInputsAndPhoneStepForRetry() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeProfileRepository().apply { failure = IllegalStateException() }
            val viewModel = SignUpViewModel(SavedStateHandle(), repository)
            viewModel.updateName("홍길동")
            viewModel.goToNextStep()
            viewModel.updatePhoneNumber("01012345678")

            viewModel.goToNextStep()
            advanceUntilIdle()

            assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
            assertEquals("홍길동", viewModel.uiState.value.name)
            assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
            assertEquals(true, viewModel.uiState.value.profileSaveFailed)

            repository.failure = null
            viewModel.goToNextStep()
            advanceUntilIdle()

            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
            assertEquals(false, viewModel.uiState.value.profileSaveFailed)
        }
}

private class FakeProfileRepository : ProfileRepository {
    var savedProfile: Profile? = null
    var failure: Exception? = null

    override suspend fun fetchProfile(): Profile? = savedProfile

    override suspend fun saveProfile(profile: Profile) {
        failure?.let { throw it }
        savedProfile = profile
    }
}
