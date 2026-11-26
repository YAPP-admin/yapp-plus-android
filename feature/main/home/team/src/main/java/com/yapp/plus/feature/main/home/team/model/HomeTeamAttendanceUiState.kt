package com.yapp.plus.feature.main.home.team.model

import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.attendance.TeamAttendanceRoster
import com.yapp.plus.domain.profile.UserPosition
import java.time.Instant
import java.time.LocalDateTime

internal data class HomeTeamAttendanceUiState(
    val sessionId: Long? = null,
    val latest: AttendanceUiState<TeamAttendancePhoto?> = AttendanceUiState.Loading,
    val isAbsent: Boolean = false,
    val guide: Boolean = false,
    val preparing: Boolean = false,
    val input: HomeTeamPhotoInput? = null,
    val roster: TeamAttendanceRoster? = null,
    val selected: Set<Long> = emptySet(),
    val isSubmitting: Boolean = false,
    val uploadFailed: Boolean = false,
    val error: Int? = null,
    val uncertain: Boolean = false,
    val receipt: HomeTeamPhotoReceiptUiModel? = null,
    val photoViewer: HomeTeamPhotoViewerUiState? = null,
) {
    val isActive: Boolean
        get() = guide || preparing || input != null || receipt != null || photoViewer != null

    val photo: TeamAttendancePhoto?
        get() = (latest as? AttendanceUiState.Content)?.value

    fun isCheckedIn(session: CurrentWeekSessionUiModel): Boolean =
        sessionId == session.sessionId &&
            session.attendanceStatus == AttendanceStatusUiModel.CheckedIn

    fun card(
        session: CurrentWeekSessionUiModel,
        now: Instant,
    ): HomeTeamPhotoSessionUiState {
        val photo = photo
        val status = session.attendanceStatus
        val localNow = LocalDateTime.ofInstant(now, teamZone)
        val start = session.startTime
        val sessionStart = start?.let { session.sessionDate?.atTime(it) }
        return when {
            status == AttendanceStatusUiModel.NotEligible -> HomeTeamPhotoSessionUiState.NotEligible
            status == AttendanceStatusUiModel.CheckedIn -> HomeTeamPhotoSessionUiState.Approved
            sessionId != session.sessionId || latest == AttendanceUiState.Loading ->
                HomeTeamPhotoSessionUiState.Loading
            latest == AttendanceUiState.Error -> HomeTeamPhotoSessionUiState.LookupFailed
            photo?.status == TeamAttendancePhotoStatus.PENDING ->
                HomeTeamPhotoSessionUiState.Pending(
                    photo.uploadedTime.format(teamTimeFormat),
                    photo.uploaderName,
                    photo.selectedMemberCount,
                )
            status == AttendanceStatusUiModel.PendingApproval -> HomeTeamPhotoSessionUiState.Reviewing
            isAbsent || photo?.status == TeamAttendancePhotoStatus.REJECTED ->
                HomeTeamPhotoSessionUiState.ResultAvailable
            sessionStart != null && localNow.isBefore(sessionStart) ->
                HomeTeamPhotoSessionUiState.BeforeStart(sessionStart.format(homeSessionStartFormat))
            session.canUploadTeamPhoto(now) && photo == null -> HomeTeamPhotoSessionUiState.AwaitingPhoto
            else -> HomeTeamPhotoSessionUiState.Closed
        }
    }

    fun selection(): HomeTeamPhotoSelectionUiState =
        HomeTeamPhotoSelectionUiState(
            members =
                roster?.members.orEmpty().map { member ->
                    HomeTeamPhotoMemberUiModel(
                        membershipId = member.membershipId,
                        name = member.name,
                        roleLabel = member.position?.label(),
                        isCurrentUser = member.membershipId == roster?.selfMembershipId,
                        isSelected = member.membershipId in selected,
                    )
                },
            canConfirm = roster != null && roster.selfMembershipId in selected && !uncertain,
            isSubmitting = isSubmitting,
            errorResource = error,
        )

    private fun UserPosition.label(): String? =
        when (this) {
            UserPosition.IOS -> "iOS"
            UserPosition.AI_NATIVE -> "AI Native"
            UserPosition.PM -> "PM"
            UserPosition.UNKNOWN -> null
            else -> name.lowercase().replaceFirstChar(Char::uppercase)
        }
}
