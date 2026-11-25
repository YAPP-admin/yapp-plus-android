package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityTitle

@Composable
fun AfterpartyBanner(onAfterpartyClick: (Long) -> Unit) {
    val viewModel: AfterpartyBannerViewModel = hiltViewModel()
    val party by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    party?.let { item ->
        AfterpartyBannerContent(item.title) { onAfterpartyClick(item.id) }
    }
}

@Composable
internal fun AfterpartyBannerContent(
    title: String,
    onClick: () -> Unit,
) {
    YappClickableSurface(
        onClick = onClick,
        modifier = Modifier.padding(bottom = 10.dp),
        color = YappMainColor.layerFill,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(Modifier.weight(1f)) { CommunityTitle(title) }
            CommunityCaption(stringResource(R.string.community_vote))
        }
    }
}
