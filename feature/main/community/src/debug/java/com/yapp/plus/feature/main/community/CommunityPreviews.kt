package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import com.yapp.plus.domain.afterparty.SettlementSummary
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilters
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.ProtectedPhoto
import com.yapp.plus.feature.main.community.components.SettlementRow
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

@Preview(showBackground = true)
@Composable
fun CommunityListPreview() =
    YappTheme {
        val summary = AfterpartySummary(party.id, party.title, schedule, AfterpartyState.OPEN, 12)
        CommunityList(
            state = CommunityListState(items = listOf(summary), loading = null),
            onFilter = {},
            onRefresh = {},
            onMore = {},
            onAfterpartyClick = {},
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

@Preview(showBackground = true)
@Composable
fun SettlementScreenPreview() =
    YappTheme {
        SettlementScreen(SettlementScreenState(detail = YappResult.Success(settlement)), {}, {}, {}, {}, {})
    }

@Preview(showBackground = true)
@Composable
fun SettlementOwnerPreview() =
    YappTheme {
        Column {
            SettlementOwner(
                SettlementScreenState(summary = YappResult.Success(SettlementSummary(48_000, 24_000, 2, 1))),
                {},
                {},
            )
        }
    }

@Preview(showBackground = true)
@Composable
private fun CommunityAfterpartyRowPreview() =
    YappTheme {
        CommunityAfterpartyRow(AfterpartySummary(1, party.title, schedule, AfterpartyState.CLOSED, 12)) {}
    }

@Preview(showBackground = true)
@Composable
private fun AfterpartyInformationPreview() = YappTheme { AfterpartyInformation(party) }

@Preview(showBackground = true)
@Composable
private fun AfterpartyVotePreview() = YappTheme { AfterpartyVote(party, false) {} }

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
