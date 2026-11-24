package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceMember
import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendanceReadRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRoster
import com.yapp.plus.domain.attendance.TeamPhotoSubmission
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    }

    private fun editing(repository: FakeTeamAttendance) =
        HomeTeamAttendanceViewModel(repository, repository, repository).also {
            it.refresh(
                CurrentWeekSessionUiModel(
                    "",
                    "",
                    "",
                    "",
                    "",
                    null,
                    AttendanceStatusUiModel.Available,
                    sessionId = 42L,
                    sessionDate = LocalDate.of(2026, 11, 28),
                    startTime = LocalTime.of(14, 0),
                    endTime = LocalTime.of(18, 0),
                    type = SessionType.TEAM,
                ),
            )
            it.openGuide(open)
            assertTrue(it.preparePhoto())
            it.photoPicked(HomeTeamPhotoInput(byteArrayOf(1), "image/jpeg", null))
        }

    private val open = Instant.parse("2026-11-28T05:00:00Z")
}

internal class FakeTeamAttendance(
    private val failSubmit: Boolean = false,
) : TeamAttendanceReadRepository,
    TeamAttendanceRepository,
    AttendanceReadRepository {
    var selected: Set<Long> = emptySet()
    var attempts = 0

    override suspend fun fetchLatestPhoto(sessionId: Long): TeamAttendancePhoto? = null

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
    ) = "key"

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
