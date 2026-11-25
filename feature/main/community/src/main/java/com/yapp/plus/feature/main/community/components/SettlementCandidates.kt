package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yapp.plus.core.designsystem.components.input.YappTextField
import com.yapp.plus.core.designsystem.components.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleSelectable
import com.yapp.plus.core.designsystem.modifier.noRippleToggleable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.feature.main.community.R
import com.yapp.plus.feature.main.community.SettlementEditorState

internal enum class SettlementRosterFilter(
    val label: Int,
) {
    All(R.string.community_all),
    Included(R.string.community_settlement),
    Excluded(R.string.community_roster_excluded),
}

@Composable
internal fun SettlementCandidates(
    state: SettlementEditorState,
    onClose: () -> Unit,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(color = YappMainColor.surface) {
            SettlementCandidateContent(state, onClose, onEdit)
        }
    }
}

@Composable
internal fun SettlementCandidateContent(
    state: SettlementEditorState,
    onClose: () -> Unit,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(SettlementRosterFilter.All) }
    val owner = stringResource(R.string.community_myself)
    val matching = state.candidates.filter { it.name.contains(query.trim(), ignoreCase = true) }
    val matchesOwner = owner.contains(query.trim(), ignoreCase = true)
    val included = matching.count { it.recipientId in state.selected } + if (matchesOwner) 1 else 0
    Column(
        Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        CommunityHeader(stringResource(R.string.community_candidates), onClose)
        SettlementCandidateFilters(
            query = query,
            filter = filter,
            total = matching.size + if (matchesOwner) 1 else 0,
            included = included,
            onQuery = { query = it },
            onFilter = { filter = it },
        )
        LazyColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            if (matchesOwner && filter != SettlementRosterFilter.Excluded) {
                item(key = "organizer") {
                    SettlementCandidateRow(owner, null, null, isChecked = true, isOwner = true) {}
                }
            }
            items(
                items =
                    matching.filter { person ->
                        val isIncluded = person.recipientId in state.selected
                        when (filter) {
                            SettlementRosterFilter.All -> true
                            SettlementRosterFilter.Included -> isIncluded
                            SettlementRosterFilter.Excluded -> !isIncluded
                        }
                    },
                key = { it.recipientId },
            ) { person ->
                SettlementCandidateRow(
                    name = person.name,
                    team = person.teamName,
                    position = person.position,
                    isChecked = person.recipientId in state.selected,
                    isOwner = false,
                ) { checked ->
                    onEdit { current ->
                        val recipients = current.selected
                        val selected = if (checked) recipients + person.recipientId else recipients - person.recipientId
                        current.copy(selected = selected)
                    }
                }
            }
        }
        SettlementEditorAction(stringResource(R.string.community_roster_next), enabled = true, onClick = onClose)
    }
}

@Composable
internal fun SettlementCandidateFilters(
    query: String,
    filter: SettlementRosterFilter,
    total: Int,
    included: Int,
    onQuery: (String) -> Unit,
    onFilter: (SettlementRosterFilter) -> Unit,
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        YappText(
            stringResource(R.string.community_candidates),
            YappTypography.scaleT5Bold,
            YappMainColor.textPrimary,
        )
        YappText(
            stringResource(R.string.community_roster_guide),
            YappTypography.scaleT4Regular,
            YappMainColor.textSecondary,
        )
        YappTextField(
            value = query,
            onValueChange = onQuery,
            placeholder = stringResource(R.string.community_search),
            variant = YappTextFieldVariant.Form,
            leadingIcon = {
                Image(
                    painterResource(R.drawable.settlement_search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
        Row(
            Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val counts = listOf(total, included, total - included)
            SettlementRosterFilter.entries.forEach { option ->
                SettlementRosterChip(
                    option = option,
                    count = counts[option.ordinal],
                    isSelected = option == filter,
                    onClick = { onFilter(option) },
                )
            }
        }
    }
}

@Composable
private fun SettlementRosterChip(
    option: SettlementRosterFilter,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        color = if (isSelected) YappMainColor.neutralSolid else YappMainColor.surface,
        shape = RoundedCornerShape(50),
        border = if (isSelected) null else BorderStroke(1.dp, YappMainColor.border),
    ) {
        Row(
            modifier =
                Modifier
                    .heightIn(min = 36.dp)
                    .noRippleSelectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            YappText(
                stringResource(option.label),
                YappTypography.scaleT4Medium,
                if (isSelected) YappMainColor.surface else YappMainColor.textPrimary,
            )
            YappText(
                count.toString(),
                YappTypography.scaleT4Medium,
                if (isSelected) YappMainColor.textFaint else YappMainColor.textSubtle,
            )
        }
    }
}

@Composable
internal fun SettlementCandidateRow(
    name: String,
    team: String?,
    position: UserPosition?,
    isChecked: Boolean,
    isOwner: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .noRippleToggleable(isChecked, enabled = !isOwner, role = Role.Checkbox, onValueChange = onToggle),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettlementCheckIcon(isChecked)
        Image(
            painterResource(settlementAvatar(position)),
            contentDescription = null,
            modifier = Modifier.size(42.dp),
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            YappText(name, YappTypography.scaleT4Bold, YappMainColor.textPrimary)
            val details = listOfNotNull(team, positionLabel(position)).joinToString("  |  ")
            if (details.isNotEmpty()) {
                YappText(details, YappTypography.scaleT3Medium, YappMainColor.textSubtle)
            }
        }
    }
}

@Composable
internal fun SettlementCheckIcon(isChecked: Boolean) {
    val resource = if (isChecked) R.drawable.settlement_check_selected else R.drawable.settlement_check_default
    Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
        Image(painterResource(resource), contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

internal fun settlementAvatar(position: UserPosition?): Int =
    when (position) {
        UserPosition.PM -> R.drawable.settlement_roster_pm
        UserPosition.DESIGN -> R.drawable.settlement_roster_design
        UserPosition.WEB -> R.drawable.settlement_roster_web
        UserPosition.MOBILE, UserPosition.ANDROID, UserPosition.IOS -> R.drawable.settlement_roster_mobile
        UserPosition.AI_NATIVE -> R.drawable.settlement_roster_ai
        else -> R.drawable.settlement_roster_server
    }

@Composable
internal fun positionLabel(position: UserPosition?): String? {
    val label =
        when (position) {
            UserPosition.PM -> R.string.community_position_pm
            UserPosition.DESIGN -> R.string.community_position_designer
            UserPosition.WEB -> R.string.community_position_web
            UserPosition.MOBILE -> R.string.community_position_mobile
            UserPosition.ANDROID -> R.string.community_position_android
            UserPosition.IOS -> R.string.community_position_ios
            UserPosition.SERVER -> R.string.community_position_server
            UserPosition.AI_NATIVE -> R.string.community_position_ai
            else -> return null
        }
    return stringResource(label)
}
