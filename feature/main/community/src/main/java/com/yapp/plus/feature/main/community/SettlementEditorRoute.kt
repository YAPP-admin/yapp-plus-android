package com.yapp.plus.feature.main.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.SettlementAccountFields
import com.yapp.plus.feature.main.community.components.SettlementCandidates
import com.yapp.plus.feature.main.community.components.SettlementEditorAction
import com.yapp.plus.feature.main.community.components.SettlementEditorPhotos
import com.yapp.plus.feature.main.community.components.SettlementPerPersonField
import com.yapp.plus.feature.main.community.components.SettlementRosterSummary
import com.yapp.plus.feature.main.community.components.SettlementTotalField

@Composable
fun SettlementEditorRoute(
    afterpartyId: Long,
    settlementId: Long?,
    onBack: () -> Unit,
    onSaved: (Int?) -> Unit,
    onSavingChanged: (Boolean) -> Unit = {},
) {
    val vm: SettlementEditorViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedUris by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    val resolver = LocalContext.current.applicationContext.contentResolver
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(SettlementDraft.MAX_PHOTOS),
        ) { uris ->
            val remaining = SettlementDraft.MAX_PHOTOS - state.photos.size
            selectedUris =
                ArrayList((selectedUris + uris.map(Uri::toString)).distinct().take(remaining.coerceAtLeast(0)))
        }
    LaunchedEffect(afterpartyId, settlementId) { vm.load(afterpartyId, settlementId) }
    LaunchedEffect(state.saving) { onSavingChanged(state.saving) }
    LaunchedEffect(state.savedId) {
        if (state.savedId != null) onSaved(state.savedRecipientCount.takeIf { settlementId == null })
    }
    SettlementEditorScreen(
        state = state,
        isEdit = settlementId != null,
        selectedUris = selectedUris,
        onBack = { if (!state.saving) onBack() },
        onEdit = vm::edit,
        onPick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onRemoveExisting = vm::removePhoto,
        onRemoveNew = { uri -> selectedUris = ArrayList(selectedUris - uri) },
        onSave = {
            val readers =
                selectedUris.map { uri ->
                    val read: suspend () -> SettlementPhotoInput? = { readSettlementPhoto(resolver, Uri.parse(uri)) }
                    read
                }
            vm.save(afterpartyId, settlementId, readers)
        },
    )
}

@Composable
internal fun SettlementEditorScreen(
    state: SettlementEditorState,
    isEdit: Boolean,
    selectedUris: List<String>,
    onBack: () -> Unit,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
    onPick: () -> Unit,
    onRemoveExisting: (Long) -> Unit,
    onRemoveNew: (String) -> Unit,
    onSave: () -> Unit,
) {
    var selectingPeople by rememberSaveable { mutableStateOf(false) }
    val editable = !state.financialLocked && !state.saving && !state.blocked
    val title = if (isEdit) R.string.community_settlement_editor_edit else R.string.community_settlement_editor
    Column(
        Modifier
            .fillMaxSize()
            .background(YappMainColor.surface)
            .imePadding(),
    ) {
        CommunityHeader(stringResource(title), onBack)
        if (state.loading) {
            CommunityLoading()
            return@Column
        }
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            SettlementRosterSummary(
                state = state,
                isEmptyForm = !isEdit && state.totalText.isEmpty(),
                enabled = editable,
                onClick = { selectingPeople = true },
            )
            if (state.financialLocked) CommunityError(AfterpartyFailure.FinancialEditLocked)
            SettlementTotalField(state, editable, onEdit)
            SettlementEditorPhotos(state, selectedUris, onPick, onRemoveExisting, onRemoveNew)
            SettlementAccountFields(state, editable, onEdit)
            SettlementPerPersonField(state, editable, onEdit)
            SettlementEditorError(state, isEdit)
        }
        SettlementEditorAction(
            text = settlementSubmitLabel(state, isEdit),
            enabled = !state.saving && !state.blocked && state.draft() != null,
            onClick = onSave,
        )
    }
    if (selectingPeople && editable) {
        SettlementCandidates(state, onClose = { selectingPeople = false }, onEdit = onEdit)
    }
}

@Composable
private fun settlementSubmitLabel(
    state: SettlementEditorState,
    isEdit: Boolean,
): String =
    when {
        state.saving -> stringResource(R.string.community_sending)
        isEdit -> stringResource(R.string.community_edit)
        else -> stringResource(R.string.community_settlement_submit, state.selected.size)
    }

@Composable
private fun SettlementEditorError(
    state: SettlementEditorState,
    isEdit: Boolean,
) {
    val failure = state.failure ?: return
    if (state.blocked && !isEdit && failure == AfterpartyFailure.Unavailable && state.bank != null) {
        CommunityCaption(stringResource(R.string.community_uncertain))
    } else {
        CommunityError(failure, isRequest = true)
    }
}
