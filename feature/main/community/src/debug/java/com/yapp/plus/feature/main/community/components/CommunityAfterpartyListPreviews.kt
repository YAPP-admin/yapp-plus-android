package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import java.time.Instant

internal val afterpartyPreviewTime: Instant = Instant.parse("2026-03-19T09:30:00Z")
internal val afterpartyPreviewItems =
    listOf(
        previewParty(1, "33기 OT 뒤풀이", "21", "20", AfterpartyState.OPEN),
        previewParty(2, "운영진 MT 회식", "25", "24", AfterpartyState.OPEN),
        previewParty(3, "32기 수료 회식", "14", "13", AfterpartyState.ENDED),
    )

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun CommunityAfterpartyTabsPreview() =
    YappTheme {
        CommunityTabs(selectedSection = CommunitySection.Afterparties, onSelect = {})
    }

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun CommunityVoteFiltersPreview() =
    YappTheme {
        CommunityVoteFilters(selectedFilter = AfterpartyFilter.ALL, onFilter = {})
    }

@Preview(showBackground = true, widthDp = 353)
@Composable
private fun CommunityAfterpartyRowPreview() =
    YappTheme {
        CommunityAfterpartyRow(afterpartyPreviewItems.first(), afterpartyPreviewTime) {}
    }

@Preview(showBackground = true)
@Composable
private fun CommunityVoteStatusPreview() =
    YappTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CommunityVoteStatus(afterpartyPreviewItems.first(), afterpartyPreviewTime)
            CommunityVoteStatus(afterpartyPreviewItems.last(), afterpartyPreviewTime)
        }
    }

@Preview(showBackground = true, widthDp = 353)
@Composable
private fun CommunityAfterpartyMetadataPreview() =
    YappTheme { CommunityAfterpartyMetadata(afterpartyPreviewItems.first()) }

private fun previewParty(
    id: Long,
    title: String,
    day: String,
    deadlineDay: String,
    state: AfterpartyState,
): AfterpartySummary =
    AfterpartySummary(
        id = id,
        title = title,
        schedule =
            AfterpartySchedule(
                startsAt = Instant.parse("2026-03-${day}T09:30:00Z"),
                startsAtDisplay = "03.$day 18:30",
                deadline = Instant.parse("2026-03-${deadlineDay}T14:59:00Z"),
                deadlineDisplay = "03.$deadlineDay 23:59",
            ),
        state = state,
        respondedCount = 42,
    )
