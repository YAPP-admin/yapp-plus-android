package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.attendance.TeamAttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamAttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoInput
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoReceiptUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoViewerUiState
import com.yapp.plus.feature.main.home.model.teamTimeFormat
import com.yapp.plus.feature.main.home.model.teamZone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
internal class HomeTeamAttendanceViewModel
    @Inject
    constructor(
        private val read: TeamAttendanceReadRepository,
        private val write: TeamAttendanceRepository,
        private val history: AttendanceReadRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(HomeTeamAttendanceUiState())
        val state = mutableState.asStateFlow()
        private var lookup: Job? = null
        private var photoLookup: Job? = null
        private var operation: Job? = null
        private var session: CurrentWeekSessionUiModel? = null

        fun refresh(target: CurrentWeekSessionUiModel?) {
            if (target?.sessionId != session?.sessionId) clear()
            session = target
            if (target == null || lookup?.isActive == true || state.value.isActive) return
            mutableState.update {
                it.copy(sessionId = target.sessionId, latest = AttendanceUiState.Loading, isAbsent = false)
            }
            if (target.attendanceStatus == AttendanceStatusUiModel.NotEligible) return
            lookup = viewModelScope.launch { fetchLatest(target) }
        }

        fun openSubmittedPhoto() {
            if (state.value.isActive || state.value.photo?.image == null) return
            loadSubmittedPhoto()
        }

        fun closeSubmittedPhoto() {
            photoLookup?.cancel()
            mutableState.update { it.copy(photoViewer = null) }
        }

        fun openGuide(now: Instant = Instant.now()) {
            val current = state.value
            if (session?.canUploadTeamPhoto(now) != true || current.isActive) return
            if (current.latest !is AttendanceUiState.Content) return
            if (current.photo?.status == TeamAttendancePhotoStatus.APPROVED) return
            mutableState.update { it.copy(guide = true, error = null) }
        }

        fun preparePhoto(): Boolean {
            if (!state.value.guide) return false
            mutableState.update { it.copy(guide = false, preparing = true) }
            return true
        }

        fun photoPicked(
            input: HomeTeamPhotoInput?,
            invalid: Boolean = false,
        ) {
            if (!state.value.preparing) return
            mutableState.update {
                it.copy(
                    preparing = false,
                    input = input,
                    guide = invalid,
                    error = R.string.home_team_photo_invalid.takeIf { invalid },
                )
            }
            if (input != null) loadRoster()
        }

        private fun loadRoster() {
            val id = session?.sessionId ?: return
            if (state.value.input == null || operation?.isActive == true) return
            mutableState.update { it.copy(preparing = true, error = null) }
            operation =
                viewModelScope.launch {
                    try {
                        val roster = read.fetchTeamRoster(id)
                        ensureActive()
                        mutableState.update {
                            it.copy(roster = roster, selected = setOf(roster.selfMembershipId))
                        }
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        return@launch
                    } finally {
                        if (isActive) mutableState.update { it.copy(preparing = false) }
                    }
                }
        }

        fun selectMember(
            id: Long,
            selected: Boolean,
        ) {
            mutableState.update { current ->
                val roster = current.roster ?: return@update current
                if (current.isSubmitting || id == roster.selfMembershipId) return@update current
                if (roster.members.none { it.membershipId == id }) return@update current
                current.copy(selected = if (selected) current.selected + id else current.selected - id)
            }
        }

        fun submit(clock: Clock = Clock.systemUTC()) {
            val target = session ?: return
            val draft = state.value
            val input = draft.input ?: return
            if (draft.roster == null || operation?.isActive == true || draft.uncertain) return
            if (!target.canUploadTeamPhoto(clock.instant())) {
                submissionFailed(uncertain = false, error = R.string.attendance_status_not_open)
                return
            }
            mutableState.update { it.copy(isSubmitting = true, error = null) }
            operation = viewModelScope.launch { submitPhoto(target, draft, input, clock) }
        }

        fun closeFlow() {
            if (state.value.isSubmitting) return
            operation?.cancel()
            mutableState.update {
                HomeTeamAttendanceUiState(
                    sessionId = it.sessionId,
                    latest = it.latest,
                    isAbsent = it.isAbsent,
                )
            }
        }

        fun clear() {
            lookup?.cancel()
            operation?.cancel()
            photoLookup?.cancel()
            lookup = null
            operation = null
            photoLookup = null
            session = null
            mutableState.value = HomeTeamAttendanceUiState()
        }

        private fun loadSubmittedPhoto() {
            val current = state.value
            val sessionId = current.sessionId ?: return
            val photoId = current.photo?.photoId ?: return
            if (photoLookup?.isActive == true) return

            mutableState.update { it.copy(photoViewer = HomeTeamPhotoViewerUiState.Loading) }
            photoLookup =
                viewModelScope.launch {
                    try {
                        val photo = read.fetchLatestPhoto(sessionId)
                        val image = photo?.image
                        val viewer =
                            when {
                                photo?.photoId != photoId || image == null -> HomeTeamPhotoViewerUiState.Unavailable
                                !image.expiresAt.isAfter(Instant.now()) -> HomeTeamPhotoViewerUiState.Error
                                else -> HomeTeamPhotoViewerUiState.Content(image)
                            }
                        ensureActive()
                        mutableState.update {
                            it.copy(latest = AttendanceUiState.Content(photo), photoViewer = viewer)
                        }
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        ensureActive()
                        mutableState.update { it.copy(photoViewer = HomeTeamPhotoViewerUiState.Error) }
                    }
                }
        }

        private suspend fun fetchLatest(target: CurrentWeekSessionUiModel) {
            try {
                val photo = read.fetchLatestPhoto(target.sessionId)
                val needsHistory =
                    target.attendanceStatus != AttendanceStatusUiModel.CheckedIn &&
                        (
                            photo?.status == TeamAttendancePhotoStatus.APPROVED ||
                                (photo == null && target.attendanceStatus == AttendanceStatusUiModel.NotOpen)
                        )
                val isAbsent =
                    needsHistory &&
                        history.fetchHistory().items.any {
                            it.sessionId == target.sessionId && it.status == AttendanceResultStatus.ABSENT
                        }
                currentCoroutineContext().ensureActive()
                mutableState.update { it.copy(latest = AttendanceUiState.Content(photo), isAbsent = isAbsent) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(latest = AttendanceUiState.Error) }
            }
        }

        private suspend fun submitPhoto(
            target: CurrentWeekSessionUiModel,
            draft: HomeTeamAttendanceUiState,
            input: HomeTeamPhotoInput,
            clock: Clock,
        ) {
            var requestedSubmission = false
            try {
                val key = write.uploadPhoto(target.sessionId, input.filename, input.contentType, input.bytes)
                currentCoroutineContext().ensureActive()
                if (session?.canUploadTeamPhoto(clock.instant()) != true) {
                    submissionFailed(uncertain = false, error = R.string.attendance_status_not_open)
                    return
                }
                requestedSubmission = true
                val result = write.submitPhoto(target.sessionId, key, draft.selected)
                currentCoroutineContext().ensureActive()
                val roster = checkNotNull(draft.roster)
                val selected = roster.members.filter { it.membershipId in draft.selected }
                val receipt =
                    HomeTeamPhotoReceiptUiModel(
                        teamName = roster.teamName,
                        uploadTimeLabel = teamTimeFormat.format(result.submittedAt.atZone(teamZone)),
                        attendeeCount = selected.size,
                        teamMemberCount = roster.members.size,
                        attendeeNamesLabel = selected.joinToString(", ") { it.name },
                    )
                mutableState.update {
                    it.copy(input = null, roster = null, receipt = receipt, isSubmitting = false)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                submissionFailed(
                    requestedSubmission,
                    if (requestedSubmission) R.string.home_team_photo_uncertain else R.string.home_submit_error,
                )
            }
        }

        private fun submissionFailed(
            uncertain: Boolean,
            error: Int,
        ) {
            mutableState.update { it.copy(isSubmitting = false, uncertain = uncertain, error = error) }
        }
    }
