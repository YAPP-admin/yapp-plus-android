package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.session.SessionType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal val teamZone: ZoneId = ZoneId.of("Asia/Seoul")
internal val teamTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal fun CurrentWeekSessionUiModel.canUploadTeamPhoto(now: Instant): Boolean {
    if (type != SessionType.TEAM) return false
    val date = sessionDate ?: return false
    val start = startTime ?: return false
    val end = endTime ?: return false
    val localNow = now.atZone(teamZone).toLocalDateTime()
    return (
        attendanceStatus == AttendanceStatusUiModel.Available ||
            attendanceStatus == AttendanceStatusUiModel.PendingApproval
    ) &&
        localNow >= date.atTime(start) &&
        localNow < date.atTime(end)
}

internal fun HomeTeamAttendanceUiState.card(
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
            HomeTeamPhotoSessionUiState.BeforeStart(sessionStart.format(startLabelFormat))
        session.canUploadTeamPhoto(now) && photo == null -> HomeTeamPhotoSessionUiState.AwaitingPhoto
        else -> HomeTeamPhotoSessionUiState.Closed
    }
}

internal fun CurrentWeekSessionUiModel.teamTimeLabel(): String =
    "${startTime?.format(teamTimeFormat).orEmpty()} - ${endTime?.format(teamTimeFormat).orEmpty()}"

internal fun HomeTeamAttendanceUiState.selection(): HomeTeamPhotoSelectionUiState =
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

private val startLabelFormat = DateTimeFormatter.ofPattern("M/d HH:mm")
