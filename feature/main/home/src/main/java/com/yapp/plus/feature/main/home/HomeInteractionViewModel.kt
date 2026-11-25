package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceLocationFailure
import com.yapp.plus.domain.attendance.AttendanceLocationPolicy
import com.yapp.plus.domain.attendance.AttendanceLocationReading
import com.yapp.plus.domain.attendance.AttendanceReportPolicy
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.session.SessionDistanceRepository
import com.yapp.plus.feature.main.home.attendance.model.HomeAttendanceAttempt
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.HomeSubmissionStage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class HomeInteractionViewModel
    @Inject
    constructor(
        private val attendanceRepository: AttendanceRepository,
        private val distanceRepository: SessionDistanceRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(HomeInteractionUiState())
        val uiState = mutableUiState.asStateFlow()
        private var attendanceSessionId: Long? = null
        private var pendingPermissionAttempt: HomeAttendanceAttempt? = null
        private var locationCheck: Job? = null

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
            val truncated = value.take(AttendanceReportPolicy.MAX_REASON_LENGTH)
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

        fun confirmReport(
            sessionDate: LocalDate?,
            startTime: LocalTime?,
            now: Instant = Instant.now(),
        ) {
            val state = mutableUiState.value
            if (!AttendanceReportPolicy.canSubmit(sessionDate, startTime, now)) {
                mutableUiState.update { it.copy(errorResource = R.string.home_report_closed) }
                return
            }
            val type = state.reportType ?: return
            if (state.isSubmitting) return
            if (state.sheet == HomeSheet.ReportType) {
                mutableUiState.update { it.copy(sheet = HomeSheet.ReportReason) }
                return
            }
            if (state.sheet != HomeSheet.ReportReason) return
            if (!state.isReportReasonValid) {
                mutableUiState.update { it.copy(errorResource = R.string.home_report_reason_required) }
                return
            }
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

        fun openAttendanceCode(sessionId: Long) {
            if (mutableUiState.value.isSubmitting || sessionId <= 0) return
            attendanceSessionId = sessionId
            mutableUiState.update {
                it.copy(
                    sheet = HomeSheet.AttendanceCode,
                    code = "",
                    errorResource = null,
                    isInvalidCode = false,
                    locationIssue = null,
                )
            }
        }

        fun dismissSheet() {
            if (mutableUiState.value.isSubmitting) return
            attendanceSessionId = null
            mutableUiState.update {
                it.copy(sheet = HomeSheet.None, errorResource = null, locationIssue = null)
            }
        }

        fun updateCode(code: String) {
            if (mutableUiState.value.isSubmitting) return
            val digits = code.filter { it in '0'..'9' }.take(CODE_LENGTH)
            mutableUiState.update {
                it.copy(
                    code = digits,
                    errorResource = null,
                    isInvalidCode = false,
                    locationIssue = null,
                )
            }
        }

        fun confirmAttendance(
            session: CurrentWeekSessionUiModel?,
            hasPrecisePermission: Boolean,
        ): Boolean {
            val attempt = createAttendanceAttempt(session) ?: return false
            if (hasPrecisePermission) {
                checkLocation(attempt)
                return false
            }
            pendingPermissionAttempt = attempt
            updateSubmissionStage(HomeSubmissionStage.RequestingLocationPermission)
            return true
        }

        private fun createAttendanceAttempt(session: CurrentWeekSessionUiModel?): HomeAttendanceAttempt? {
            val state = uiState.value
            if (state.isSubmitting || state.sheet != HomeSheet.AttendanceCode) return null
            val code = state.code.takeIf { it.length == CODE_LENGTH }?.toIntOrNull()
            if (code == null) {
                mutableUiState.update {
                    it.copy(
                        errorResource = R.string.home_code_required,
                        isInvalidCode = true,
                        locationIssue = null,
                    )
                }
                return null
            }
            val coordinates = session?.coordinates?.takeIf { it.isValid }
            if (session == null ||
                session.sessionId != attendanceSessionId ||
                session.attendanceStatus != AttendanceStatusUiModel.Available ||
                coordinates == null
            ) {
                showLocationIssue(HomeAttendanceLocationIssue.SessionUnavailable)
                return null
            }
            return HomeAttendanceAttempt(session.sessionId, code, coordinates)
        }

        fun onAttendancePermissionResult(
            session: CurrentWeekSessionUiModel?,
            hasPrecisePermission: Boolean,
            hasApproximatePermission: Boolean,
        ) {
            val attempt = pendingPermissionAttempt ?: return
            pendingPermissionAttempt = null
            when {
                !attempt.matches(
                    session?.sessionId,
                    session?.coordinates,
                    session?.attendanceStatus == AttendanceStatusUiModel.Available,
                ) ->
                    showLocationIssue(HomeAttendanceLocationIssue.SessionUnavailable)
                hasPrecisePermission -> checkLocation(attempt)
                hasApproximatePermission ->
                    showLocationIssue(HomeAttendanceLocationIssue.PrecisePermissionRequired)
                else -> showLocationIssue(HomeAttendanceLocationIssue.PermissionDenied)
            }
        }

        fun cancelAttendanceLocation() {
            val stage = uiState.value.submissionStage
            if (stage != HomeSubmissionStage.CheckingLocation &&
                stage != HomeSubmissionStage.RequestingLocationPermission
            ) {
                return
            }
            pendingPermissionAttempt = null
            locationCheck?.cancel()
            locationCheck = null
            updateSubmissionStage(HomeSubmissionStage.Idle)
        }

        fun onLocationSettingsUnavailable() {
            if (uiState.value.isSubmitting) return
            showLocationIssue(HomeAttendanceLocationIssue.SettingsUnavailable)
        }

        private fun checkLocation(attempt: HomeAttendanceAttempt) {
            updateSubmissionStage(HomeSubmissionStage.CheckingLocation)
            locationCheck =
                viewModelScope.launch {
                    val distance = measureAttendanceDistance(attempt)
                    ensureActive()
                    val issue = AttendanceLocationPolicy.blockingReason(distance)?.toUiIssue()
                    if (issue == null) submitVerifiedLocation(attempt) else showLocationIssue(issue)
                }
        }

        private suspend fun measureAttendanceDistance(attempt: HomeAttendanceAttempt): AttendanceLocationReading =
            try {
                distanceRepository.fetchPreciseDistanceTo(attempt.coordinates)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
            }

        private fun submitVerifiedLocation(attempt: HomeAttendanceAttempt) {
            val sessionId = attempt.sessionId
            submit(
                operation = { attendanceRepository.verifyAttendance(sessionId, attempt.code) },
                onSuccess = { state, _ ->
                    state.copy(
                        sheet = HomeSheet.None,
                        verifiedSessionId = sessionId,
                    )
                },
            )
        }

        private fun updateSubmissionStage(stage: HomeSubmissionStage) {
            mutableUiState.update {
                it.copy(
                    submissionStage = stage,
                    errorResource = null,
                    isInvalidCode = false,
                    locationIssue = null,
                )
            }
        }

        private fun showLocationIssue(issue: HomeAttendanceLocationIssue) {
            mutableUiState.update {
                it.copy(
                    submissionStage = HomeSubmissionStage.Idle,
                    errorResource = null,
                    isInvalidCode = false,
                    locationIssue = issue,
                )
            }
        }

        private fun <T> submit(
            operation: suspend () -> T,
            onSuccess: (HomeInteractionUiState, T) -> HomeInteractionUiState,
        ) {
            updateSubmissionStage(HomeSubmissionStage.Submitting)
            viewModelScope.launch {
                try {
                    val result = operation()
                    mutableUiState.update { onSuccess(it, result) }
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (exception: Exception) {
                    showSubmissionError(exception)
                } finally {
                    mutableUiState.update { it.copy(submissionStage = HomeSubmissionStage.Idle) }
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
            mutableUiState.update { it.copy(verifiedSessionId = null) }
        }

        private companion object {
            const val CODE_LENGTH = 4
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

private fun AttendanceLocationFailure.toUiIssue(): HomeAttendanceLocationIssue =
    when (this) {
        AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED ->
            HomeAttendanceLocationIssue.PrecisePermissionRequired
        AttendanceLocationFailure.LOCATION_DISABLED -> HomeAttendanceLocationIssue.LocationDisabled
        AttendanceLocationFailure.TIMED_OUT -> HomeAttendanceLocationIssue.TimedOut
        AttendanceLocationFailure.UNAVAILABLE -> HomeAttendanceLocationIssue.Unavailable
        AttendanceLocationFailure.INSUFFICIENT_ACCURACY ->
            HomeAttendanceLocationIssue.InsufficientAccuracy
        AttendanceLocationFailure.OUT_OF_RANGE -> HomeAttendanceLocationIssue.OutOfRange
    }
