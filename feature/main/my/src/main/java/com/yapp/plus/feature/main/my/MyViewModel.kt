package com.yapp.plus.feature.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.my.MyRepository
import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyProfileSummaryState
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import com.yapp.plus.feature.main.my.model.toAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.toSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyViewModel
    @Inject
    constructor(
        private val profileRepository: ProfileRepository,
        private val myRepository: MyRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(MyUiState())
        val uiState = mutableUiState.asStateFlow()
        private var profileRequest: Job? = null
        private var testProfileRequest: Job? = null
        private var attendanceHistoryRequest: Job? = null

        init {
            retry()
            retryTestProfile()
        }

        fun retry() {
            if (profileRequest?.isActive == true) return
            mutableUiState.update { it.copy(submittedProfile = MyProfileUiState.Loading) }
            profileRequest =
                viewModelScope.launch {
                    try {
                        val profile = profileRepository.fetchProfile()?.toUiState() ?: MyProfileUiState.Empty
                        mutableUiState.update { it.copy(submittedProfile = profile) }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableUiState.update { it.copy(submittedProfile = MyProfileUiState.Error) }
                    }
                }
        }

        fun retryTestProfile() {
            if (testProfileRequest?.isActive == true) return
            mutableUiState.update { it.copy(testProfile = MyProfileSummaryState.Loading) }
            testProfileRequest =
                viewModelScope.launch {
                    try {
                        val profile = myRepository.fetchProfile().toSummaryState()
                        mutableUiState.update { it.copy(testProfile = profile) }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableUiState.update {
                            it.copy(testProfile = MyProfileSummaryState.Error)
                        }
                    }
                }
        }

        fun toggleAttendanceHistory() {
            val isExpanded = !mutableUiState.value.isAttendanceHistoryExpanded
            mutableUiState.update { it.copy(isAttendanceHistoryExpanded = isExpanded) }
            if (isExpanded && mutableUiState.value.attendanceHistory == MyAttendanceHistoryState.NotRequested) {
                retryAttendanceHistory()
            }
        }

        fun retryAttendanceHistory() {
            if (attendanceHistoryRequest?.isActive == true) return
            mutableUiState.update { it.copy(attendanceHistory = MyAttendanceHistoryState.Loading) }
            attendanceHistoryRequest =
                viewModelScope.launch {
                    try {
                        val history = myRepository.fetchAttendanceHistory().toAttendanceHistoryState()
                        mutableUiState.update { it.copy(attendanceHistory = history) }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableUiState.update {
                            it.copy(attendanceHistory = MyAttendanceHistoryState.Error)
                        }
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
