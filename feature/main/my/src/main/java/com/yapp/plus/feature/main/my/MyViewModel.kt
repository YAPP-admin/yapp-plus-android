package com.yapp.plus.feature.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyViewModel
    @Inject
    constructor(
        private val profileRepository: ProfileRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow<MyProfileUiState>(MyProfileUiState.Loading)
        val uiState = mutableUiState.asStateFlow()
        private var profileRequest: Job? = null

        init {
            retry()
        }

        fun retry() {
            if (profileRequest?.isActive == true) return
            mutableUiState.value = MyProfileUiState.Loading
            profileRequest =
                viewModelScope.launch {
                    try {
                        mutableUiState.value =
                            profileRepository.fetchProfile()?.toUiState()
                                ?: MyProfileUiState.Empty
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableUiState.value = MyProfileUiState.Error
                    }
                }
        }

        private fun Profile.toUiState() =
            MyProfileUiState.Content(
                MyProfileUiModel(
                    name = name.value,
                    phoneNumber = phoneNumber.value,
                ),
            )
    }
