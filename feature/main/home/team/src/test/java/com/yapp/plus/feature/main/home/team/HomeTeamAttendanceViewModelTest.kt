package com.yapp.plus.feature.main.home.team

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceMember
import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.attendance.TeamAttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRoster
import com.yapp.plus.domain.attendance.TeamPhotoSubmission
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoInput
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoViewerUiState
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class HomeTeamAttendanceViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()

    @Test
    fun submitsOnlyDuringSessionAndAlwaysIncludesSelf() {
        val repository = FakeTeamAttendance()
        val viewModel = editing(repository)
        viewModel.selectMember(11L, selected = false)
        viewModel.selectMember(12L, selected = true)

        viewModel.submit(Clock.fixed(Instant.parse("2026-11-28T09:00:00Z"), ZoneOffset.UTC))
        assertEquals(0, repository.attempts)

        viewModel.submit(Clock.fixed(open, ZoneOffset.UTC))
        assertEquals(setOf(11L, 12L), repository.selected)
        assertNotNull(viewModel.state.value.receipt)
    }

    @Test
    fun preventsResubmissionWhenFinalResponseIsLost() {
        val repository = FakeTeamAttendance(failSubmit = true)
        val viewModel = editing(repository)
        repeat(2) { viewModel.submit(Clock.fixed(open, ZoneOffset.UTC)) }
        assertEquals(1, repository.attempts)
        assertTrue(viewModel.state.value.uncertain)
        assertFalse(viewModel.state.value.uploadFailed)
    }

    @Test
    fun retriesUploadFailureWithTheSamePhotoAndMembers() {
        val repository = FakeTeamAttendance().apply { failUpload = true }
        val viewModel = editing(repository)
        val photo = checkNotNull(viewModel.state.value.input)
        viewModel.selectMember(12L, selected = true)

        viewModel.submit(Clock.fixed(open, ZoneOffset.UTC))

        assertTrue(viewModel.state.value.uploadFailed)
        assertEquals(photo, viewModel.state.value.input)
        assertEquals(0, repository.attempts)

        repository.failUpload = false
        viewModel.submit(Clock.fixed(open, ZoneOffset.UTC))

        assertFalse(viewModel.state.value.uploadFailed)
        assertEquals(2, repository.uploadAttempts)
        assertEquals(setOf(11L, 12L), repository.selected)
        assertNotNull(viewModel.state.value.receipt)
    }

    @Test
    fun receiptShowsRealUploadTimeAfterTheMatchingPhotoIsLoaded() {
        val repository = FakeTeamAttendance()
        val viewModel = editing(repository)
        val response = CompletableDeferred<TeamAttendancePhoto?>()
        repository.photoResponse = response

        viewModel.submit(Clock.fixed(open, ZoneOffset.UTC))

        val receiptBeforeLookup = checkNotNull(viewModel.state.value.receipt)
        assertEquals("14:00", receiptBeforeLookup.submittedTimeLabel)
        assertNull(receiptBeforeLookup.uploadTimeLabel)

        response.complete(photo(1L, Instant.MAX).copy(uploadedTime = LocalTime.of(13, 57)))

        val receiptAfterLookup = checkNotNull(viewModel.state.value.receipt)
        assertEquals("13:57", receiptAfterLookup.uploadTimeLabel)
    }

    @Test
    fun receiptKeepsSubmissionTimeWhenLatestPhotoIsDifferent() {
        val repository = FakeTeamAttendance()
        val viewModel = editing(repository)
        repository.latestPhoto = photo(2L, Instant.MAX).copy(uploadedTime = LocalTime.of(13, 57))

        viewModel.submit(Clock.fixed(open, ZoneOffset.UTC))

        val receipt = checkNotNull(viewModel.state.value.receipt)
        assertEquals("14:00", receipt.submittedTimeLabel)
        assertNull(receipt.uploadTimeLabel)
    }

    @Test
    fun approvalNeedsPersonalCheckInAndStaysAcknowledgedForTheSession() {
        val repository =
            FakeTeamAttendance().apply {
                latestPhoto = photo(1L, Instant.MAX).copy(status = TeamAttendancePhotoStatus.APPROVED)
            }
        val savedState = SavedStateHandle()
        val viewModel = home(repository, savedState)

        assertFalse(viewModel.state.value.shouldShowApproval(session))

        val approvedSession = session.copy(attendanceStatus = AttendanceStatusUiModel.CheckedIn)
        viewModel.refresh(approvedSession)
        assertTrue(viewModel.state.value.shouldShowApproval(approvedSession))

        viewModel.acknowledgeApproval(approvedSession.sessionId)
        viewModel.clear()
        viewModel.refresh(approvedSession)
        assertFalse(viewModel.state.value.shouldShowApproval(approvedSession))

        val recreatedViewModel = home(repository, savedState)
        recreatedViewModel.refresh(approvedSession)
        assertFalse(recreatedViewModel.state.value.shouldShowApproval(approvedSession))

        val nextSession = approvedSession.copy(sessionId = 43L)
        recreatedViewModel.refresh(nextSession)
        assertTrue(recreatedViewModel.state.value.shouldShowApproval(nextSession))
    }

    @Test
    fun refreshesExpiredPhotoLinkWhenViewerIsReopened() {
        val expiredPhoto = photo(1, Instant.EPOCH)
        val repository = FakeTeamAttendance().apply { latestPhoto = expiredPhoto }
        val viewModel = home(repository)

        viewModel.openSubmittedPhoto()
        assertEquals(HomeTeamPhotoViewerUiState.Error, viewModel.state.value.photoViewer)
        assertEquals(2, repository.photoReads)

        val currentPhoto = photo(1, Instant.MAX)
        repository.latestPhoto = currentPhoto
        viewModel.closeSubmittedPhoto()
        viewModel.openSubmittedPhoto()
        assertEquals(
            HomeTeamPhotoViewerUiState.Content(checkNotNull(currentPhoto.image)),
            viewModel.state.value.photoViewer,
        )
        assertEquals(3, repository.photoReads)

        viewModel.closeSubmittedPhoto()
        assertNull(viewModel.state.value.photoViewer)
    }

    @Test
    fun asksForFreshLinkEvenWhenCardLinkIsValid() {
        val previousPhoto = photo(1, Instant.MAX)
        val repository = FakeTeamAttendance().apply { latestPhoto = previousPhoto }
        val viewModel = home(repository)
        repository.latestPhoto = previousPhoto.copy(image = null)

        viewModel.openSubmittedPhoto()
        assertEquals(HomeTeamPhotoViewerUiState.Unavailable, viewModel.state.value.photoViewer)
        assertNull(
            viewModel.state.value.photo
                ?.image,
        )
        viewModel.closeSubmittedPhoto()

        viewModel.openSubmittedPhoto()
        assertNull(viewModel.state.value.photoViewer)
        assertEquals(2, repository.photoReads)
    }

    @Test
    fun leavesViewerUnavailableWhenSubmittedPhotoWasReplaced() {
        val repository = FakeTeamAttendance().apply { latestPhoto = photo(1, Instant.MAX) }
        val viewModel = home(repository)
        val replacement = photo(2, Instant.MAX)
        repository.latestPhoto = replacement

        viewModel.openSubmittedPhoto()

        assertEquals(HomeTeamPhotoViewerUiState.Unavailable, viewModel.state.value.photoViewer)
        assertEquals(replacement, viewModel.state.value.photo)
    }

    @Test
    fun closingViewerCancelsAnInFlightPhotoLookup() {
        val repository = FakeTeamAttendance().apply { latestPhoto = photo(1, Instant.MAX) }
        val viewModel = home(repository)
        val response = CompletableDeferred<TeamAttendancePhoto?>()
        repository.photoResponse = response

        viewModel.openSubmittedPhoto()
        assertEquals(HomeTeamPhotoViewerUiState.Loading, viewModel.state.value.photoViewer)
        viewModel.closeSubmittedPhoto()
        response.complete(photo(2, Instant.MAX))

        assertNull(viewModel.state.value.photoViewer)
        assertEquals(
            1L,
            viewModel.state.value.photo
                ?.photoId,
        )
    }

    private fun editing(repository: FakeTeamAttendance) =
        home(repository).also {
            it.openGuide(open)
            assertTrue(it.preparePhoto())
            it.photoPicked(HomeTeamPhotoInput(byteArrayOf(1), "image/jpeg", null))
        }

    private fun home(
        repository: FakeTeamAttendance,
        savedState: SavedStateHandle = SavedStateHandle(),
    ) = HomeTeamAttendanceViewModel(repository, repository, repository, savedState).also {
        it.refresh(session)
    }

    private val session =
        CurrentWeekSessionUiModel(
            weekStartDateLabel = "",
            weekEndDateLabel = "",
            dateLabel = "",
            name = "",
            startTimeLabel = "",
            location = "팀별 자율 장소",
            attendanceStatus = AttendanceStatusUiModel.Available,
            sessionId = 42L,
            sessionDate = LocalDate.of(2026, 11, 28),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(18, 0),
            type = SessionType.TEAM,
        )

    private fun photo(
        photoId: Long,
        expiresAt: Instant,
    ) = TeamAttendancePhoto(
        photoId = photoId,
        teamId = 7L,
        teamName = "팀",
        status = TeamAttendancePhotoStatus.PENDING,
        image = TeamAttendancePhotoImage("https://example.invalid/photo/$photoId", expiresAt),
        uploadedAt = open,
        uploadedTime = LocalTime.of(14, 0),
        submittedAt = open,
        uploaderName = "본인",
        selectedMemberCount = 2,
    )

    private val open = Instant.parse("2026-11-28T05:00:00Z")
}

internal class FakeTeamAttendance(
    private val failSubmit: Boolean = false,
) : TeamAttendanceReadRepository,
    TeamAttendanceRepository,
    AttendanceReadRepository {
    var selected: Set<Long> = emptySet()
    var attempts = 0
    var uploadAttempts = 0
    var failUpload = false

    var latestPhoto: TeamAttendancePhoto? = null
    var photoResponse: CompletableDeferred<TeamAttendancePhoto?>? = null
    var photoReads = 0

    override suspend fun fetchLatestPhoto(sessionId: Long): TeamAttendancePhoto? {
        photoReads++
        val response = photoResponse ?: return latestPhoto
        return response.await()
    }

    override suspend fun fetchTeamRoster(sessionId: Long) =
        TeamAttendanceRoster(
            7,
            "팀",
            11,
            listOf(TeamAttendanceMember(11, "본인", null), TeamAttendanceMember(12, "팀원", null)),
        )

    override suspend fun uploadPhoto(
        sessionId: Long,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): String {
        uploadAttempts++
        check(!failUpload)
        return "key"
    }

    override suspend fun submitPhoto(
        sessionId: Long,
        imageKey: String,
        membershipIds: Set<Long>,
    ): TeamPhotoSubmission {
        attempts++
        check(!failSubmit)
        selected = membershipIds
        return TeamPhotoSubmission(1, Instant.parse("2026-11-28T05:00:00Z"))
    }

    override suspend fun fetchScore() = null

    override suspend fun fetchHistory(cursor: String?) = AttendanceHistoryPage(0, 0, emptyList(), null)
}
