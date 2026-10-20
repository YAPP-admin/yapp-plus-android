package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.ReportChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeInteractionViewModel
    @Inject
    constructor(
        private val attendanceRepository: AttendanceRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(HomeInteractionUiState())
        val uiState = mutableUiState.asStateFlow()

        fun openSheet(sheet: HomeSheet) {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update {
                it.copy(
                    sheet = sheet,
                    code = "",
                    reason = "",
                    reportChoice = null,
                    reportType = null,
                    errorResource = null,
                    isInvalidCode = false,
                )
            }
        }

        fun dismissSheet() {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update { it.copy(sheet = HomeSheet.None, errorResource = null) }
        }

        fun updateCode(code: String) {
            if (mutableUiState.value.isSubmitting) return
            val digits = code.filter { it in '0'..'9' }.take(CODE_LENGTH)
            mutableUiState.update {
                it.copy(code = digits, errorResource = null, isInvalidCode = false)
            }
        }

        fun updateReason(reason: String) {
            if (mutableUiState.value.isSubmitting || reason.length > MAX_REASON_LENGTH) return
            mutableUiState.update { it.copy(reason = reason, errorResource = null) }
        }

        fun selectReport(choice: ReportChoice) {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update { it.copy(reportChoice = choice) }
        }

        fun selectLateDuration(type: AttendanceReportType) {
            if (type !in LATE_TYPES || mutableUiState.value.isSubmitting) return
            mutableUiState.update { it.copy(reportType = type) }
        }

        fun advanceReport() {
            val state = mutableUiState.value
            if (state.isSubmitting) return
            if (state.sheet == HomeSheet.LateDuration) {
                if (state.reportType in LATE_TYPES) {
                    mutableUiState.update { it.copy(sheet = HomeSheet.ReportReason) }
                }
                return
            }
            advanceReportChoice(state.reportChoice ?: return)
        }

        private fun advanceReportChoice(choice: ReportChoice) {
            val type =
                when (choice) {
                    ReportChoice.Late -> null
                    ReportChoice.EarlyLeave -> AttendanceReportType.EARLY_LEAVE
                    ReportChoice.Absent -> AttendanceReportType.ABSENT
                }
            mutableUiState.update {
                it.copy(
                    sheet = if (type == null) HomeSheet.LateDuration else HomeSheet.ReportReason,
                    reportType = type,
                )
            }
        }

        fun verifyAttendance(sessionId: Long) {
            val state = mutableUiState.value
            if (state.isSubmitting || sessionId <= 0) return
            if (state.code.length != CODE_LENGTH) {
                mutableUiState.update {
                    it.copy(errorResource = R.string.home_code_required, isInvalidCode = true)
                }
                return
            }
            submit(
                operation = {
                    attendanceRepository.verifyAttendance(
                        sessionId,
                        state.code.toInt(),
                    )
                },
                onSuccess = { state, _ ->
                    state.copy(
                        sheet = HomeSheet.None,
                        attendedSessionId = sessionId,
                        isAttendanceComplete = true,
                    )
                },
            )
        }

        fun submitReport() {
            val state = mutableUiState.value
            val type = state.reportType ?: return
            if (state.isSubmitting || state.reason.isBlank()) return
            submit(
                operation = { attendanceRepository.submitReport(type, state.reason.trim()) },
                onSuccess = { state, sessionId ->
                    state.copy(
                        sheet = HomeSheet.None,
                        isReportComplete = true,
                        reportedSessionId = sessionId,
                    )
                },
            )
        }

        private fun <T> submit(
            operation: suspend () -> T,
            onSuccess: (HomeInteractionUiState, T) -> HomeInteractionUiState,
        ) {
            mutableUiState.update { it.copy(isSubmitting = true, errorResource = null) }
            viewModelScope.launch {
                try {
                    val result = operation()
                    mutableUiState.update { onSuccess(it, result) }
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (exception: Exception) {
                    showSubmissionError(exception)
                } finally {
                    mutableUiState.update { it.copy(isSubmitting = false) }
                }
            }
        }

        private fun showSubmissionError(exception: Exception) {
            val isInvalidCode = (exception as? AttendanceException)?.isInvalidCode == true
            val errorResource =
                if (isInvalidCode) {
                    R.string.home_code_invalid
                } else {
                    R.string.home_submit_error
                }
            mutableUiState.update {
                it.copy(isInvalidCode = isInvalidCode, errorResource = errorResource)
            }
        }

        fun acknowledgeAttendanceComplete() {
            mutableUiState.update { it.copy(isAttendanceComplete = false) }
        }

        fun acknowledgeReportComplete() {
            mutableUiState.update { it.copy(isReportComplete = false) }
        }

        private companion object {
            const val CODE_LENGTH = 4
            const val MAX_REASON_LENGTH = 30
            val LATE_TYPES =
                setOf(
                    AttendanceReportType.ONE_HOUR_LATE,
                    AttendanceReportType.THREE_HOUR_LATE,
                )
        }
    }
