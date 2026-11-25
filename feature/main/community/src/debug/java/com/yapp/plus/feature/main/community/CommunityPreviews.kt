package com.yapp.plus.feature.main.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementAccount
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementPhoto
import com.yapp.plus.domain.afterparty.SettlementSummary
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilters
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.ProtectedPhoto
import com.yapp.plus.feature.main.community.components.SettlementPayment
import com.yapp.plus.feature.main.community.components.SettlementRow
import com.yapp.plus.feature.main.community.components.SettlementTotals
import com.yapp.plus.feature.main.community.components.afterpartyPreviewItems
import com.yapp.plus.feature.main.community.components.afterpartyPreviewTime
import java.time.Instant

private val schedule =
    AfterpartySchedule(
        Instant.parse("2099-03-21T09:30:00Z"),
        "03.21 [토] 18:30",
        Instant.parse("2099-03-20T14:59:00Z"),
        "03.20 [금] 23:59",
    )
private val party =
    AfterpartyDetail(
        1,
        "OT 뒤풀이",
        "OT가 끝나고 다 같이 저녁 먹어요.",
        schedule,
        AfterpartyState.OPEN,
        AfterpartyResponse.UNANSWERED,
        null,
    )
private val row = SettlementItem(3, 1, "김야뿌", false, 3, 24_000, SettlementPaymentStatus.UNPAID)
private val settlement =
    SettlementDetail(
        row,
        SettlementAccount(SettlementBank.TOSS, "001-1234", "김야뿌"),
        72_000,
        24_000,
        2,
        0,
        emptySet(),
        emptyList(),
        0,
    )
private val person = AfterpartyPerson(2, "박야뿌", "1팀", null, false)
private val finishedParty = party.copy(state = AfterpartyState.ENDED, response = AfterpartyResponse.ATTENDING)
private val finishedSummary = AfterpartySummary(party.id, party.title, schedule, AfterpartyState.ENDED, 31)
private val ownerRow = row.copy(id = 4, isCreator = true, paymentStatus = null)
private val ownerSummary = SettlementSummary(48_000, 24_000, 2, 1)
private val paidSettlement =
    settlement.copy(
        item = row.copy(id = 5, creatorName = "이야뿌", paymentStatus = SettlementPaymentStatus.PAID),
        account = settlement.account.copy(holder = "이야뿌"),
        photos = listOf(SettlementPhoto(1, null, null), SettlementPhoto(2, null, null)),
    )

@Preview(showBackground = true, widthDp = 393, heightDp = 690)
@Composable
fun CommunityListPreview() =
    YappTheme {
        CommunityList(
            state = CommunityListState(items = afterpartyPreviewItems, loading = null),
            onFilter = {},
            onRefresh = {},
            onMore = {},
            onAfterpartyClick = {},
            now = afterpartyPreviewTime,
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 690)
@Composable
fun CommunityListEmptyOpenPreview() =
    YappTheme {
        CommunityList(
            state = CommunityListState(items = emptyList(), filter = AfterpartyFilter.OPEN, loading = null),
            onFilter = {},
            onRefresh = {},
            onMore = {},
            onAfterpartyClick = {},
            now = afterpartyPreviewTime,
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 690)
@Composable
fun CommunityListEmptyClosedPreview() =
    YappTheme {
        CommunityList(
            state = CommunityListState(items = emptyList(), filter = AfterpartyFilter.CLOSED, loading = null),
            onFilter = {},
            onRefresh = {},
            onMore = {},
            onAfterpartyClick = {},
            now = afterpartyPreviewTime,
        )
    }

@Preview(showBackground = true)
@Composable
fun AfterpartyScreenPreview() =
    YappTheme {
        AfterpartyScreen(
            AfterpartyScreenState(detail = YappResult.Success(party), settlements = YappResult.Success(listOf(row))),
            {},
            {},
            {},
            {},
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 740)
@Composable
fun FinishedAfterpartySettlementPreview() =
    YappTheme {
        AfterpartyScreen(
            state =
                AfterpartyScreenState(
                    detail = YappResult.Success(finishedParty),
                    settlements = YappResult.Success(listOf(ownerRow, row)),
                    ownerSummaries = mapOf(ownerRow.id to ownerSummary),
                ),
            onBack = {},
            onSettlementClick = {},
            onAddSettlement = {},
            onVote = {},
            settlementRecipientCount = 31,
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 740)
@Composable
fun SettlementScreenPreview() =
    YappTheme {
        SettlementScreen(
            state =
                SettlementScreenState(
                    detail = YappResult.Success(settlement),
                    afterparty = finishedParty,
                    payments = YappResult.Success(listOf(settlement, paidSettlement)),
                ),
            onBack = {},
            onEdit = {},
            onFilter = {},
            onPayment = { _, _ -> },
            onReload = {},
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 740)
@Composable
fun SettlementOwnerPreview() =
    YappTheme {
        SettlementScreen(
            state =
                SettlementScreenState(
                    detail = YappResult.Success(settlement.copy(item = ownerRow)),
                    afterparty = finishedParty,
                    summary = YappResult.Success(ownerSummary),
                    people = YappResult.Success(listOf(person.copy(position = UserPosition.DESIGN))),
                ),
            onBack = {},
            onEdit = {},
            onFilter = {},
            onPayment = { _, _ -> },
            onReload = {},
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 650)
@Composable
fun MyPayableSettlementsPreview() =
    YappTheme {
        MySettlementsScreen(
            YappResult.Success(
                listOf(AfterpartySettlements(finishedSummary, listOf(row, paidSettlement.item), emptyMap())),
            ),
            {},
            { _, _ -> },
        )
    }

@Preview(showBackground = true, widthDp = 393)
@Composable
fun MyReceivingSettlementsPreview() =
    YappTheme {
        Column(Modifier.fillMaxSize().background(YappMainColor.surface)) {
            CommunityHeader("회식 정산") {}
            SettlementListTabs(true) {}
            SettlementListFilters(true, null) {}
            Column(Modifier.padding(horizontal = 20.dp)) {
                MySettlementListRow(
                    MySettlementRow(
                        afterparty = finishedSummary,
                        settlementId = ownerRow.id,
                        status = SettlementPaymentStatus.UNPAID,
                        summary = ownerSummary,
                    ),
                    isReceiving = true,
                    onClick = {},
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
private fun AfterpartyInformationPreview() = YappTheme { AfterpartyInformation(party) }

@Preview(showBackground = true)
@Composable
private fun AfterpartyContentPreview() = YappTheme { AfterpartyContent(party, false, {}) {} }

@Preview(showBackground = true)
@Composable
private fun AfterpartyBannerPreview() = YappTheme { AfterpartyBannerContent(party.title) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementRowPreview() = YappTheme { SettlementRow(row) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementTotalsPreview() = YappTheme { SettlementTotals(SettlementSummary(48_000, 24_000, 2, 1)) }

@Preview(showBackground = true)
@Composable
private fun SettlementPaymentPreview() = YappTheme { Column { SettlementPayment(settlement, false) {} } }

@Preview(showBackground = true)
@Composable
private fun CommunityPersonPreview() = YappTheme { CommunityPerson(person) }

@Preview(showBackground = true)
@Composable
private fun CommunityFiltersPreview() =
    YappTheme {
        CommunityFilters(AfterpartyFilter.entries, AfterpartyFilter.ALL, { it.name }) {}
    }

@Preview(showBackground = true)
@Composable
private fun CommunityHeaderPreview() = YappTheme { CommunityHeader("참석 여부") {} }

@Preview(showBackground = true)
@Composable
private fun CommunityErrorPreview() = YappTheme { CommunityError() }

@Preview(showBackground = true)
@Composable
private fun CommunityLoadingPreview() = YappTheme { CommunityLoading() }

@Preview(showBackground = true)
@Composable
private fun CommunityTypographyPreview() =
    YappTheme {
        Column {
            CommunityTitle("뒤풀이")
            CommunityCaption("03.21 [토] 18:30")
        }
    }

@Preview
@Composable
private fun ProtectedPhotoPreview() = YappTheme { ProtectedPhoto(null, Modifier) }
