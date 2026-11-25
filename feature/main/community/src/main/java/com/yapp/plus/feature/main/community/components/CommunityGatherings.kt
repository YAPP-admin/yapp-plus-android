package com.yapp.plus.feature.main.community.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.feature.main.community.R

internal enum class CommunityGatheringFilter(
    @StringRes val label: Int,
) {
    All(R.string.community_all),
    SmallGroup(R.string.community_small_groups),
    Meetup(R.string.community_meetups),
}

@Composable
internal fun CommunityGatherings() {
    var selectedFilter by rememberSaveable { mutableStateOf(CommunityGatheringFilter.SmallGroup) }

    Column(Modifier.fillMaxSize()) {
        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (filter in CommunityGatheringFilter.entries) {
                CommunityFilterChip(
                    label = stringResource(filter.label),
                    isSelected = filter == selectedFilter,
                    onClick = { selectedFilter = filter },
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            CommunityEmptyState(text = stringResource(R.string.community_gatherings_preparing))
        }
    }
}
