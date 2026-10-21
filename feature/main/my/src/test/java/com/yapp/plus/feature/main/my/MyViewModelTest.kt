package com.yapp.plus.feature.main.my

import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchesAndMapsSavedProfile() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = MyViewModel(FakeProfileRepository())

            assertEquals(
                MyProfileUiState.Content(MyProfileUiModel("홍길동", "01012345678")),
                viewModel.uiState.value,
            )
        }

    @Test
    fun emptyProfileAndFetchFailureHaveDistinctRetryableStates() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeProfileRepository().apply { profile = null }
            val viewModel = MyViewModel(repository)
            assertEquals(MyProfileUiState.Empty, viewModel.uiState.value)

            repository.failure = IllegalStateException()
            viewModel.retry()
            assertEquals(MyProfileUiState.Error, viewModel.uiState.value)

            repository.failure = null
            repository.profile = Profile(Name("김YAPP"), PhoneNumber("01098765432"))
            viewModel.retry()
            assertEquals(
                MyProfileUiState.Content(MyProfileUiModel("김YAPP", "01098765432")),
                viewModel.uiState.value,
            )
        }
}

private class FakeProfileRepository : ProfileRepository {
    var profile: Profile? = Profile(Name("홍길동"), PhoneNumber("01012345678"))
    var failure: Exception? = null

    override suspend fun fetchProfile(): Profile? {
        failure?.let { throw it }
        return profile
    }

    override suspend fun saveProfile(profile: Profile) {
        this.profile = profile
    }
}
