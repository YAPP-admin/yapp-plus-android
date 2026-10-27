package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.RegistrationFieldError
import com.yapp.plus.domain.auth.RegistrationFieldError.Reason
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.model.SignUpStep
import com.yapp.plus.feature.signup.model.SignUpUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpViewModelTest {
    @Test
    fun updatesOneInputWithoutClearingTheOther() {
        val viewModel = createViewModel()

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
        val viewModel = createViewModel(handle)
        viewModel.updateName("이름")
        viewModel.updatePhoneNumber("01012345678")

        val restoredHandle =
            SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
        val restoredViewModel = createViewModel(restoredHandle)

        assertEquals(viewModel.uiState.value, restoredViewModel.uiState.value)
        restoredViewModel.updatePhoneNumber("")
        assertEquals("이름", restoredViewModel.uiState.value.name)
        assertEquals("", restoredHandle.get<String>(SignUpViewModel.PHONE_NUMBER_KEY))
    }

    @Test
    fun preservesInputWithoutPhoneFormatValidation() {
        val viewModel = createViewModel()

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
            createViewModel(
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
        val viewModel = createViewModel()
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
            createViewModel(
                SavedStateHandle(mapOf(SignUpViewModel.STEP_KEY to SignUpStep.Complete.name)),
            )
        assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
    }

    @Test
    fun blankNameCannotAdvance() {
        val viewModel = createViewModel()
        viewModel.updateName("  ")
        viewModel.goToNextStep()
        assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
    }

    @Test
    fun nameErrorReturnsToNameAndResubmitsWithTheSavedPhone() =
        runBlocking {
            val repository = nameErrorRepository()
            val viewModel = readyToSubmit(repository)
            viewModel.submit("reg-token")

            assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
            assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
            assertEquals(Reason.TooLong, viewModel.uiState.value.nameError)
            assertTrue(viewModel.uiState.value.canSubmitFromName)
            assertEquals(null, viewModel.uiState.value.failure)

            viewModel.updateName("이름")
            viewModel.submit("reg-token")
            assertEquals(1, repository.registrations.size)
            viewModel.updateName("수정한 이름")
            assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
            repository.registrationResult = AuthResult.SignedIn
            viewModel.submit("reg-token")

            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
            assertEquals(2, repository.registrations.size)
            assertEquals(
                Name("수정한 이름") to PhoneNumber("+821012345678"),
                repository.registrations.last(),
            )
        }

    @Test
    fun nameCorrectionRestoresThePhoneAndSkipsThePhoneStepAfterRecreation() =
        runBlocking {
            val repository = nameErrorRepository()
            val handle = SavedStateHandle()
            val viewModel = readyToSubmit(repository, handle)
            viewModel.submit("reg-token")
            val restoredHandle =
                SavedStateHandle(handle.keys().associateWith { key -> handle.get<Any?>(key) })
            val restored = createViewModel(restoredHandle, repository)

            assertEquals(SignUpStep.Name, restored.uiState.value.step)
            assertTrue(restored.uiState.value.canSubmitFromName)
            assertEquals("01012345678", restored.uiState.value.phoneNumber)
            restored.updateName("수정한 이름")
            repository.registrationResult = AuthResult.SignedIn
            restored.submit("reg-token")
            assertEquals(SignUpStep.Complete, restored.uiState.value.step)
        }

    @Test
    fun errorsForBothFieldsRequireCorrectingNameThenPhone() =
        runBlocking {
            val repository = nameErrorRepository()
            repository.registrationResult =
                AuthResult.Failed(
                    AuthFailure.InvalidRequest,
                    listOf(
                        RegistrationFieldError.Name(Reason.TooLong),
                        RegistrationFieldError.PhoneNumber(Reason.InvalidFormat),
                    ),
                )
            val viewModel = readyToSubmit(repository)
            viewModel.submit("reg-token")
            assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
            assertFalse(viewModel.uiState.value.canSubmitFromName)

            viewModel.updateName("수정한 이름")
            viewModel.goToNextStep()
            assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
            assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
            viewModel.submit("reg-token")
            assertEquals(1, repository.registrations.size)

            viewModel.updatePhoneNumber("01098765432")
            repository.registrationResult = AuthResult.SignedIn
            viewModel.submit("reg-token")
            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
        }

    @Test
    fun duplicatePhoneStaysOnPhoneAndAllowsCorrection() =
        runBlocking {
            val repository = FakeAuthRepository()
            repository.registrationResult =
                AuthResult.Failed(
                    AuthFailure.UserAlreadyExists,
                    listOf(RegistrationFieldError.PhoneNumber(Reason.AlreadyExists)),
                )
            val viewModel = readyToSubmit(repository)
            viewModel.submit("reg-token")

            assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
            assertEquals(Reason.AlreadyExists, viewModel.uiState.value.phoneNumberError)
            assertEquals(null, viewModel.uiState.value.failure)
            viewModel.updatePhoneNumber("01098765432")
            repository.registrationResult = AuthResult.SignedIn
            viewModel.submit("reg-token")
            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
        }

    @Test
    fun temporaryFailureWhileCorrectingNameKeepsThePhoneForRetry() =
        runBlocking {
            val repository = nameErrorRepository()
            val viewModel = readyToSubmit(repository)
            viewModel.submit("reg-token")
            viewModel.updateName("수정한 이름")
            repository.registrationResult = AuthResult.Failed(AuthFailure.Unavailable)
            viewModel.submit("reg-token")

            assertEquals(SignUpStep.Name, viewModel.uiState.value.step)
            assertTrue(viewModel.uiState.value.canSubmitFromName)
            assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
            viewModel.dismissFailure()
            repository.registrationResult = AuthResult.SignedIn
            viewModel.submit("reg-token")
            assertEquals(SignUpStep.Complete, viewModel.uiState.value.step)
        }

    @Test
    fun changingPhoneAfterNameErrorRequiresThePhoneStepAgain() =
        runBlocking {
            val viewModel = readyToSubmit(nameErrorRepository())
            viewModel.submit("reg-token")
            viewModel.updatePhoneNumber("01098765432")
            viewModel.updateName("수정한 이름")
            viewModel.goToNextStep()
            assertFalse(viewModel.uiState.value.canSubmitFromName)
            assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
        }

    @Test
    fun pendingSubmissionPreventsDuplicateRequestsAndInputChanges() =
        runBlocking {
            val repository = FakeAuthRepository()
            val pendingResult = CompletableDeferred<AuthResult>()
            repository.onRegister = { pendingResult.await() }
            val viewModel = readyToSubmit(repository)
            val submission =
                launch(start = CoroutineStart.UNDISPATCHED) { viewModel.submit("reg-token") }

            viewModel.updateName("다른 이름")
            viewModel.updatePhoneNumber("01098765432")
            viewModel.goBack()
            viewModel.submit("reg-token")
            assertEquals(SignUpStep.Phone, viewModel.uiState.value.step)
            assertEquals("이름", viewModel.uiState.value.name)
            assertEquals("01012345678", viewModel.uiState.value.phoneNumber)
            assertEquals(1, repository.registrations.size)
            pendingResult.complete(AuthResult.SignedIn)
            submission.join()
        }

    @Test
    fun clearingInputsAlsoClearsTheNameResubmissionState() =
        runBlocking {
            val handle = SavedStateHandle()
            val viewModel = readyToSubmit(nameErrorRepository(), handle)
            viewModel.submit("reg-token")
            viewModel.clearSignUpInputs()
            assertEquals(SignUpUiState(), viewModel.uiState.value)
            assertEquals(null, handle.get<Boolean>(SignUpViewModel.CAN_SUBMIT_FROM_NAME_KEY))
        }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        repository: FakeAuthRepository = FakeAuthRepository(),
    ) = SignUpViewModel(savedStateHandle, repository)

    private fun readyToSubmit(
        repository: FakeAuthRepository,
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): SignUpViewModel {
        val viewModel = createViewModel(savedStateHandle, repository)
        viewModel.updateName("이름")
        viewModel.goToNextStep()
        viewModel.updatePhoneNumber("01012345678")
        return viewModel
    }

    private fun nameErrorRepository() =
        FakeAuthRepository().apply {
            registrationResult =
                AuthResult.Failed(
                    AuthFailure.InvalidRequest,
                    listOf(RegistrationFieldError.Name(Reason.TooLong)),
                )
        }
}
