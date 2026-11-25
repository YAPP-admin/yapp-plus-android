package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappDetailHeader
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.feature.main.community.R

@Composable
internal fun CommunityHeader(
    title: String,
    onBack: () -> Unit,
) {
    YappDetailHeader(title, stringResource(R.string.community_back), onBack)
}

@Composable
internal fun CommunityTitle(text: String) {
    YappText(text, YappTypography.scaleT6Bold, YappMainColor.textPrimary)
}

@Composable
internal fun CommunityCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    YappText(text, YappTypography.scaleT3Medium, YappMainColor.textSecondary, modifier)
}

@Composable
internal fun CommunityLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = YappMainColor.brand)
    }
}

@Composable
internal fun CommunityError(
    reason: AfterpartyFailure? = null,
    isRequest: Boolean = false,
) {
    val message =
        when (reason) {
            AfterpartyFailure.VotingClosed -> R.string.community_voting_closed
            AfterpartyFailure.NotFound -> R.string.community_missing
            AfterpartyFailure.AuthenticationRequired, AfterpartyFailure.Forbidden,
            AfterpartyFailure.ResponseRequired,
            -> R.string.community_forbidden
            AfterpartyFailure.InvalidRequest -> R.string.community_invalid
            AfterpartyFailure.RevisionConflict -> R.string.community_conflict
            AfterpartyFailure.FinancialEditLocked -> R.string.community_locked
            else -> if (isRequest) R.string.community_request_error else R.string.community_loading_error
        }
    YappText(stringResource(message), YappTypography.scaleT3Medium, YappMainColor.critical)
}

@Composable
internal fun <T> CommunityFilters(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelected: (T) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { value ->
            val active = value == selected
            YappClickableSurface(
                onClick = { onSelected(value) },
                shape = RoundedCornerShape(50),
                color = if (active) YappMainColor.textPrimary else YappMainColor.layerFill,
            ) {
                YappText(
                    text = label(value),
                    style = YappTypography.scaleT3Medium,
                    color = if (active) YappMainColor.surface else YappMainColor.textPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
internal fun CommunityPerson(
    person: AfterpartyPerson,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            CommunityTitle(person.name)
            if (person.isMe) CommunityCaption(stringResource(R.string.community_myself))
        }
        val details = listOfNotNull(person.teamName, person.position?.takeUnless { it.name == "UNKNOWN" }?.name)
        if (details.isNotEmpty()) CommunityCaption(details.joinToString(" · "))
    }
}
