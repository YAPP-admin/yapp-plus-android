package com.yapp.plus.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.feature.setting.NotificationSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NotificationSettingsViewModel
    @Inject
    constructor(
        private val registration: PushRegistration,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(NotificationSettingsUiState())
        val state = mutableState.asStateFlow()
        private var request: Job? = null
        private var failedSelection: Boolean? = null

        fun refresh() {
            if (request?.isActive == true) return
            mutableState.update { it.copy(isLoading = true) }
            request =
                viewModelScope.launch {
                    try {
                        val enabled = registration.fetchEnabled()
                        mutableState.update {
                            it.copy(
                                isEnabled = enabled,
                                hasLoadError = false,
                                hasUpdateError = failedSelection != null,
                            )
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableState.update { it.copy(hasLoadError = true) }
                    } finally {
                        mutableState.update { it.copy(isLoading = false) }
                        updatePermission()
                    }
                }
        }

        fun updateEnabled(enabled: Boolean) {
            if (mutableState.value.isUpdating || mutableState.value.isEnabled == null) return
            request?.cancel()
            failedSelection = enabled
            mutableState.update {
                it.copy(
                    isLoading = false,
                    isUpdating = true,
                    hasLoadError = false,
                    hasUpdateError = false,
                )
            }
            request =
                viewModelScope.launch {
                    try {
                        registration.updateEnabled(enabled)
                        mutableState.update { it.copy(isEnabled = enabled) }
                        failedSelection = null
                    } catch (exception: CancellationException) {
                        failedSelection = null
                        throw exception
                    } catch (_: Exception) {
                        mutableState.update { it.copy(hasUpdateError = true) }
                    } finally {
                        mutableState.update { it.copy(isUpdating = false) }
                        updatePermission()
                    }
                }
        }

        fun retry() {
            if (mutableState.value.hasLoadError) {
                refresh()
                return
            }
            val selection = failedSelection
            if (selection != null) {
                updateEnabled(selection)
            } else {
                refresh()
            }
        }

        private fun updatePermission() {
            mutableState.update { it.copy(isPermissionGranted = registration.hasPermission()) }
        }
    }
