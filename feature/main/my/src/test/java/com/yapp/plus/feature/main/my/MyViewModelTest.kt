package com.yapp.plus.feature.main.my

import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceHistoryEntry
import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceStaffDecision
import com.yapp.plus.domain.auth.UserProfileRepository
import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserMembership
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.profile.UserProfile
import com.yapp.plus.domain.profile.UserProfileReadRepository
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState
import com.yapp.plus.feature.main.my.model.MyAttendanceStatus
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.toUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeAttendanceReadRepository()
    private val noMembership = UserProfileReadRepository { UserProfile(1, "서버 이름", null) }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun profileLoadsServerNameAndMembership() =
        runTest(dispatcher) {
            val remote =
                UserProfileReadRepository {
                    UserProfile(
                        userId = 1,
                        name = "서버 이름",
                        membership = UserMembership(MembershipStatus.DEFERRED, 12, 29, UserPosition.IOS),
                    )
                }
            val model = MyViewModel(repository, UserProfileRepository { "저장 이름" }, remote)

            model.fetchProfile()
            advanceUntilIdle()

            assertEquals(
                MyProfileUiState.Content(
                    MyProfileUiModel("서버 이름", MembershipStatus.DEFERRED, 29, UserPosition.IOS),
                ),
                model.uiState.value.profile,
            )
        }

    @Test
    fun profileFailureKeepsSessionNameAndCanRetryToValidNoMembership() =
        runTest(dispatcher) {
            var calls = 0
            val gate = CompletableDeferred<Unit>()
            val remote =
                UserProfileReadRepository {
                    if (++calls == 1) {
                        gate.await()
                        error("Unavailable")
                    }
                    noMembership.fetchProfile()
                }
            val model = MyViewModel(repository, UserProfileRepository { "저장 이름" }, remote)

            repeat(2) { model.fetchProfile() }
            runCurrent()
            assertEquals(1, calls)
            assertEquals(MyProfileUiState.Loading("저장 이름"), model.uiState.value.profile)
            gate.complete(Unit)
            advanceUntilIdle()
            assertEquals(MyProfileUiState.Error("저장 이름"), model.uiState.value.profile)

            model.fetchProfile()
            advanceUntilIdle()
            assertEquals(
                MyProfileUiState.Content(MyProfileUiModel("서버 이름")),
                model.uiState.value.profile,
            )
        }

    @Test
    fun profileRequestCancelsOnExitAndReloadsOnReturn() =
        runTest(dispatcher) {
            var calls = 0
            var wasCancelled = false
            val remote =
                UserProfileReadRepository {
                    if (++calls == 1) {
                        try {
                            awaitCancellation()
                        } finally {
                            wasCancelled = true
                        }
                    }
                    noMembership.fetchProfile()
                }
            val model = MyViewModel(repository, UserProfileRepository { null }, remote)

            model.fetchProfile()
            runCurrent()
            model.cancelProfileRequest()
            model.fetchProfile()
            advanceUntilIdle()

            assertTrue(wasCancelled)
            assertEquals(2, calls)
            assertEquals(
                MyProfileUiState.Content(MyProfileUiModel("서버 이름")),
                model.uiState.value.profile,
            )
        }

    @Test
    fun scoreFailureCanRetryAndDoesNotPreventHistoryLoading() =
        runTest(dispatcher) {
            repository.scoreError = IllegalStateException()
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchScore()
            model.fetchHistory()
            advanceUntilIdle()
            assertEquals(MyAttendanceScoreState.Error, model.uiState.value.score)
            assertTrue(model.uiState.value.attendanceHistory is MyAttendanceHistoryState.Content)
            repository.scoreError = null
            model.fetchScore()
            advanceUntilIdle()
            assertEquals(
                MyAttendanceScoreState.Content(repository.score!!),
                model.uiState.value.score,
            )
            assertEquals(2, repository.scoreRequests)
        }

    @Test
    fun ineligibleScoreDoesNotBecomeZeroPoints() =
        runTest(dispatcher) {
            repository.score = null
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchScore()
            advanceUntilIdle()
            assertEquals(MyAttendanceScoreState.Ineligible, model.uiState.value.score)
        }

    @Test
    fun concurrentRepeatedRequestsAreCoalescedPerResource() =
        runTest(dispatcher) {
            repository.gate = CompletableDeferred()
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            repeat(3) {
                model.fetchScore()
                model.fetchHistory()
            }
            runCurrent()
            assertEquals(1, repository.scoreRequests)
            assertEquals(listOf<String?>(null), repository.historyCursors)
            repository.gate!!.complete(Unit)
            advanceUntilIdle()
        }

    @Test
    fun paginationFailureKeepsRecordsAndRetriesSameCursor() =
        runTest(dispatcher) {
            repository.pages[null] = page(listOf(3, 2), "next")
            repository.pages["next"] = page(listOf(2, 1), null)
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchHistory()
            advanceUntilIdle()
            repository.historyError = IllegalStateException()
            model.fetchNextPage()
            advanceUntilIdle()
            assertTrue(history(model).hasLoadMoreError)
            assertEquals(listOf(3L, 2L), history(model).items.map { it.sessionId })
            repository.historyError = null
            model.fetchNextPage()
            advanceUntilIdle()
            assertEquals(listOf(3L, 2L, 1L), history(model).items.map { it.sessionId })
            assertFalse(history(model).hasLoadMoreError)
            model.fetchNextPage()
            assertEquals(listOf(null, "next", "next"), repository.historyCursors)
        }

    @Test
    fun refreshRestartsAtFirstPageAndDropsStaleRecords() =
        runTest(dispatcher) {
            repository.pages[null] = page(listOf(3), "next")
            repository.pages["next"] = page(listOf(2), null)
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchHistory()
            advanceUntilIdle()
            model.fetchNextPage()
            advanceUntilIdle()
            repository.pages[null] = page(listOf(4), null)
            model.fetchHistory()
            advanceUntilIdle()
            assertEquals(listOf(4L), history(model).items.map { it.sessionId })
            assertEquals(listOf(null, "next", null), repository.historyCursors)
        }

    @Test
    fun cyclicCursorStopsAutomaticPaging() =
        runTest(dispatcher) {
            repository.pages[null] = page(listOf(3), "next")
            repository.pages["next"] = page(listOf(2), "next")
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchHistory()
            advanceUntilIdle()
            model.fetchNextPage()
            advanceUntilIdle()
            assertTrue(history(model).hasLoadMoreError)
            assertEquals(listOf(3L), history(model).items.map { it.sessionId })
        }

    @Test
    fun cancelledPagingPreservesExistingContent() =
        runTest(dispatcher) {
            repository.pages[null] = page(listOf(3), "next")
            val model =
                MyViewModel(
                    repository,
                    UserProfileRepository { null },
                    noMembership,
                )
            model.fetchHistory()
            advanceUntilIdle()
            repository.gate = CompletableDeferred()
            model.fetchNextPage()
            runCurrent()
            model.viewModelScope.cancel()
            advanceUntilIdle()
            assertFalse(history(model).isLoadingMore)
            assertFalse(history(model).hasLoadMoreError)
            assertEquals(listOf(3L), history(model).items.map { it.sessionId })
        }

    @Test
    fun pendingReviewAndManualPresentAreNotRenderedAsAbsence() {
        val entries =
            listOf(
                AttendanceHistoryEntry(
                    1,
                    "검토",
                    AttendanceResultStatus.UNDECIDED,
                    AttendanceStaffDecision.PENDING,
                ),
                AttendanceHistoryEntry(
                    2,
                    "정정",
                    AttendanceResultStatus.PRESENT,
                    AttendanceStaffDecision.REJECTED,
                ),
                AttendanceHistoryEntry(3, "복합", AttendanceResultStatus.LATE_AND_EARLY_LEAVE, null),
            )
        val state = AttendanceHistoryPage(12, 7, entries, null).toUiState()
        assertEquals(5, state.summary.remainingSessionCount)
        assertEquals(
            listOf(
                MyAttendanceStatus.Pending,
                MyAttendanceStatus.Present,
                MyAttendanceStatus.LateAndEarlyLeave,
            ),
            state.items.map { it.status },
        )
    }

    private fun history(model: MyViewModel): MyAttendanceHistoryState.Content =
        model.uiState.value.attendanceHistory as MyAttendanceHistoryState.Content

    private fun page(
        ids: List<Int>,
        nextCursor: String?,
    ) = AttendanceHistoryPage(
        totalSessionCount = 12,
        startedSessionCount = 6,
        items =
            ids.map {
                AttendanceHistoryEntry(it.toLong(), "세션 $it", AttendanceResultStatus.PRESENT, null)
            },
        nextCursor = nextCursor,
    )
}
