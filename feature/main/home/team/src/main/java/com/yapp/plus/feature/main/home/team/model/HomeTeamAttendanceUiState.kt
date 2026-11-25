package com.yapp.plus.feature.main.home.team.model

import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.attendance.TeamAttendanceRoster
import java.time.Instant
import java.time.LocalDateTime

data class HomeTeamAttendanceUiState(
    val sessionId: Long? = null,
    val latest: AttendanceUiState<TeamAttendancePhoto?> = AttendanceUiState.Loading,
    val isAbsent: Boolean = false,
    val guide: Boolean = false,
    val preparing: Boolean = false,
    val input: HomeTeamPhotoInput? = null,
    val roster: TeamAttendanceRoster? = null,
    val selected: Set<Long> = emptySet(),
    val isSubmitting: Boolean = false,
    val error: Int? = null,
    val uncertain: Boolean = false,
    val receipt: HomeTeamPhotoReceiptUiModel? = null,
    val photoViewer: HomeTeamPhotoViewerUiState? = null,
) {
    val isActive: Boolean
        get() = guide || preparing || input != null || receipt != null || photoViewer != null

    val photo: TeamAttendancePhoto?
        get() = (latest as? AttendanceUiState.Content<TeamAttendancePhoto?>)?.value

    fun card(
        session: CurrentWeekSessionUiModel,
        now: Instant,
    ): HomeTeamPhotoSessionUiState {
        val photo = photo
        val status = session.attendanceStatus
        val localNow = LocalDateTime.ofInstant(now, teamZone)
        val date = session.sessionDate
        val start = session.startTime
        val end = session.endTime
        val isTeamSessionTime = date != null && start != null && end != null &&
            localNow >= date.atTime(start) && localNow < date.atTime(end)

        return when {
            status == AttendanceStatusUiModel.NotEligible -> HomeTeamPhotoSessionUiState.NotEligible
            status == AttendanceStatusUiModel.CheckedIn && photo != null && photo.status == TeamAttendancePhotoStatus.APPROVED -> HomeTeamPhotoSessionUiState.Approved
            status == AttendanceStatusUiModel.CheckedIn && photo != null && photo.status == TeamAttendancePhotoStatus.REJECTED -> HomeTeamPhotoSessionUiState.Rejected
            status == AttendanceStatusUiModel.CheckedIn && photo != null && photo.status == TeamAttendancePhotoStatus.PENDING -> HomeTeamPhotoSessionUiState.Reviewing
            status == AttendanceStatusUiModel.CheckedIn -> HomeTeamPhotoSessionUiState.NotSubmitted
            status == AttendanceStatusUiModel.PendingApproval && photo != null && photo.status == TeamAttendancePhotoStatus.APPROVED -> HomeTeamPhotoSessionUiState.Approved
            status == AttendanceStatusUiModel.PendingApproval && photo != null && photo.status == TeamAttendancePhotoStatus.REJECTED -> HomeTeamPhotoSessionUiState.Rejected
            status == AttendanceStatusUiModel.PendingApproval && photo != null && photo.status == TeamAttendancePhotoStatus.PENDING -> HomeTeamPhotoSessionUiState.Reviewing
            status == AttendanceStatusUiModel.PendingApproval -> HomeTeamPhotoSessionUiState.Reviewing
            status == AttendanceStatusUiModel.Available && isTeamSessionTime -> HomeTeamPhotoSessionUiState.Active
            status == AttendanceStatusUiModel.Available -> HomeTeamPhotoSessionUiState.NotSubmitted
            status == AttendanceStatusUiModel.NotOpen && isTeamSessionTime -> HomeTeamPhotoSessionUiState.Active
            else -> HomeTeamPhotoSessionUiState.NotOpen
        }
    }
}

enum class HomeTeamPhotoSessionUiState {
    NotEligible,
    NotOpen,
    Active,
    NotSubmitted,
    Reviewing,
    Approved,
    Rejected,
    LookupFailed,
}

val teamZone: java.time.ZoneId = java.time.ZoneId.of("Asia/Seoul")
val teamTimeFormat: java.time.format.DateTimeFormatter = com.yapp.plus.core.ui.formatter.AppDateFormatter.timeHourMinute
