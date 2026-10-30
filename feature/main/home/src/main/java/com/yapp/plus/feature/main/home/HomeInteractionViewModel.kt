package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
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

        fun openReport() {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update {
                it.copy(
                    sheet = HomeSheet.ReportType,
                    reportType = null,
                    reportReason = "",
                    errorResource = null,
                    isInvalidCode = false,
                )
            }
        }

        fun selectReportType(type: AttendanceReportType) {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update { it.copy(reportType = type, errorResource = null) }
        }

        fun updateReportReason(value: String) {
            if (mutableUiState.value.isSubmitting) return
            val truncated = value.take(MAX_REASON_LENGTH)
            val reason =
                if (truncated.lastOrNull()?.isHighSurrogate() ==
                    true
                ) {
                    truncated.dropLast(1)
                } else {
                    truncated
                }
            mutableUiState.update { it.copy(reportReason = reason, errorResource = null) }
        }

        fun confirmReport() {
            val state = mutableUiState.value
            val type = state.reportType ?: return
            if (state.isSubmitting) return
            if (state.sheet == HomeSheet.ReportType) {
                mutableUiState.update { it.copy(sheet = HomeSheet.ReportReason) }
                return
            }
            if (state.sheet != HomeSheet.ReportReason) return
            submit(
                operation = { attendanceRepository.submitReport(type, state.reportReason.trim()) },
                onSuccess = {
                    current,
                    _,
                    ->
                    current.copy(sheet = HomeSheet.None, isReportComplete = true)
                },
            )
        }

        fun acknowledgeReportComplete() {
            mutableUiState.update { it.copy(isReportComplete = false) }
        }

        fun openAttendanceCode() {
            if (mutableUiState.value.isSubmitting) return
            mutableUiState.update {
                it.copy(
                    sheet = HomeSheet.AttendanceCode,
                    code = "",
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
                    reportErrorResource((exception as? AttendanceException)?.code)
                }
            mutableUiState.update {
                it.copy(isInvalidCode = isInvalidCode, errorResource = errorResource)
            }
        }

        fun acknowledgeAttendanceComplete() {
            mutableUiState.update { it.copy(isAttendanceComplete = false) }
        }

        private companion object {
            const val CODE_LENGTH = 4
            const val MAX_REASON_LENGTH = 30
        }
    }

private fun reportErrorResource(code: String?): Int =
    when (code) {
        "ATTENDANCE_REPORT_NOT_ELIGIBLE" -> R.string.home_report_not_eligible
        "ATTENDANCE_SESSION_NOT_FOUND" -> R.string.home_report_no_session
        "ATTENDANCE_REPORT_UNAVAILABLE" -> R.string.home_report_unavailable
        "ATTENDANCE_REPORT_SESSION_CHANGED", "ATTENDANCE_REPORT_SESSION_CONFLICT" ->
            R.string.home_report_session_changed
        "INVALID_ATTENDANCE_REPORT" -> R.string.home_report_invalid
        else -> R.string.home_submit_error
    }
