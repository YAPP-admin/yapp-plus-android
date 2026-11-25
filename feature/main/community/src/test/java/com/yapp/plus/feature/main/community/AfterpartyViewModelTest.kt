package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyPage
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class AfterpartyViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()

    @Test
    fun readsAttendeesOnlyAfterServerAcceptsAResponse() {
        val parties = FakeAfterparties()
        val vm = AfterpartyViewModel(parties, EmptySettlements())
        vm.load(9)
        assertNull(vm.state.value.attendees)
        assertEquals(0, parties.attendeeRequests)

        vm.vote(AfterpartyResponse.NOT_ATTENDING, Instant.parse("2030-10-01T00:00:00Z"))

        assertEquals(
            AfterpartyResponse.NOT_ATTENDING,
            vm.state.value.detail
                .valueOrNull()
                ?.response,
        )
        assertEquals(1, parties.attendeeRequests)
    }

    @Test
    fun doesNotPutAVoteAtTheDeadline() {
        val parties = FakeAfterparties()
        val vm = AfterpartyViewModel(parties, EmptySettlements())
        vm.load(9)

        vm.vote(AfterpartyResponse.ATTENDING, parties.detail.schedule.deadline)

        assertEquals(AfterpartyFailure.VotingClosed, vm.state.value.failure)
        assertEquals(0, parties.votes)
    }
}

internal class FakeAfterparties : AfterpartyRepository {
    var detail =
        AfterpartyDetail(
            id = 9,
            title = "뒤풀이",
            content = "내용",
            schedule =
                AfterpartySchedule(
                    Instant.parse("2030-10-03T12:00:00Z"),
                    "10.03 [목] 21:00",
                    Instant.parse("2030-10-02T12:00:00Z"),
                    "10.02 [수] 21:00",
                ),
            state = AfterpartyState.OPEN,
            response = AfterpartyResponse.UNANSWERED,
            attendingCount = null,
        )
    var attendeeRequests = 0
    var votes = 0

    override suspend fun fetchAfterparties(
        filter: AfterpartyFilter,
        cursor: String?,
    ) = YappResult.Success(AfterpartyPage<AfterpartySummary>(emptyList(), null))

    override suspend fun fetchAfterparty(id: Long) = YappResult.Success(detail)

    override suspend fun fetchAttendees(
        id: Long,
        cursor: String?,
    ): YappResult.Success<AfterpartyPage<AfterpartyPerson>> {
        attendeeRequests++
        return YappResult.Success(AfterpartyPage(emptyList(), null))
    }

    override suspend fun updateResponse(
        id: Long,
        response: AfterpartyResponse,
    ): YappResult.Success<AfterpartyResponse> {
        votes++
        detail = detail.copy(response = response, attendingCount = 0)
        return YappResult.Success(response)
    }
}

internal class EmptySettlements : SettlementRepository {
    override suspend fun fetchSettlements(
        afterpartyId: Long,
        cursor: String?,
    ) = YappResult.Success(AfterpartyPage<SettlementItem>(emptyList(), null))

    override suspend fun fetchSettlement(
        afterpartyId: Long,
        settlementId: Long,
    ): Nothing = unexpected()

    override suspend fun fetchEdit(
        afterpartyId: Long,
        settlementId: Long,
    ): Nothing = unexpected()

    override suspend fun fetchCandidates(
        afterpartyId: Long,
        settlementId: Long?,
        cursor: String?,
    ): Nothing = unexpected()

    override suspend fun uploadPhoto(
        afterpartyId: Long,
        settlementId: Long?,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): Nothing = unexpected()

    override suspend fun createSettlement(
        afterpartyId: Long,
        draft: SettlementDraft,
    ): Nothing = unexpected()

    override suspend fun updateSettlement(
        afterpartyId: Long,
        settlementId: Long,
        expectedRevision: Long,
        draft: SettlementDraft,
    ): Nothing = unexpected()

    override suspend fun updatePayment(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
    ): Nothing = unexpected()

    override suspend fun fetchSummary(
        afterpartyId: Long,
        settlementId: Long,
    ): Nothing = unexpected()

    override suspend fun fetchRecipients(
        afterpartyId: Long,
        settlementId: Long,
        status: SettlementPaymentStatus,
        cursor: String?,
    ): Nothing = unexpected()

    private fun unexpected(): Nothing = error("Unexpected settlement call")
}
