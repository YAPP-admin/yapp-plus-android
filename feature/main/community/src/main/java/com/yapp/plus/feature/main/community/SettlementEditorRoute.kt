package com.yapp.plus.feature.main.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.modifier.WithoutRipple
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.ProtectedPhoto
import com.yapp.plus.feature.main.community.components.bankLabel
import java.time.Instant

@Composable
fun SettlementEditorRoute(
    afterpartyId: Long,
    settlementId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
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
    LaunchedEffect(state.savedId) { if (state.savedId != null) onSaved() }
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
    Column(Modifier.fillMaxSize()) {
        CommunityHeader(stringResource(title), onBack)
        if (state.loading) {
            CommunityLoading()
            return@Column
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YappClickableSurface(
                onClick = { selectingPeople = true },
                enabled = editable,
                shape = RoundedCornerShape(12.dp),
                color = YappMainColor.layerFill,
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CommunityTitle(stringResource(R.string.community_selected_count, state.peopleCount))
                    val names = state.candidates.filter { it.recipientId in state.selected }.map { it.name }
                    CommunityCaption(
                        (listOf(stringResource(R.string.community_myself)) + names.take(3)).joinToString(", "),
                    )
                }
            }
            if (state.financialLocked) CommunityError(AfterpartyFailure.FinancialEditLocked)
            SettlementInput(
                value = state.totalText,
                label = stringResource(R.string.community_total_amount),
                enabled = editable,
                keyboardType = KeyboardType.Number,
                onChange = { value -> onEdit { it.copy(totalText = value.filter(Char::isDigit)) } },
            )
            SettlementEditorPhotos(state, selectedUris, onPick, onRemoveExisting, onRemoveNew)
            SettlementAccountFields(state, editable, onEdit)
            SettlementInput(
                value = state.amountText,
                label = stringResource(R.string.community_per_person),
                enabled = editable,
                keyboardType = KeyboardType.Number,
                onChange = { value -> onEdit { it.copy(amountOverride = value.filter(Char::isDigit)) } },
            )
            val failure = state.failure
            if (state.blocked && !isEdit && failure == AfterpartyFailure.Unavailable && state.bank != null) {
                CommunityCaption(stringResource(R.string.community_uncertain))
            } else if (failure != null) {
                CommunityError(failure, isRequest = true)
            }
        }
        val submitLabel =
            when {
                state.saving -> stringResource(R.string.community_sending)
                isEdit -> stringResource(R.string.community_settlement_save)
                else -> stringResource(R.string.community_settlement_submit, state.selected.size)
            }
        YappButton(
            text = submitLabel,
            onClick = onSave,
            enabled = !state.saving && !state.blocked && state.draft() != null,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
    if (selectingPeople) {
        SettlementCandidates(state, onClose = { selectingPeople = false }, onEdit = onEdit)
    }
}

@Composable
internal fun SettlementCandidates(
    state: SettlementEditorState,
    onClose: () -> Unit,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(color = YappMainColor.surface) {
            Column(Modifier.fillMaxSize()) {
                CommunityHeader(stringResource(R.string.community_candidates), onClose)
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettlementInput(query, stringResource(R.string.community_search), true, onChange = { query = it })
                    for (person in state.candidates.filter { it.name.contains(query.trim(), ignoreCase = true) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WithoutRipple {
                                Checkbox(
                                    checked = person.recipientId in state.selected,
                                    onCheckedChange = { checked ->
                                        onEdit {
                                            val ids =
                                                if (checked) {
                                                    it.selected + person.recipientId
                                                } else {
                                                    it.selected -
                                                        person.recipientId
                                                }
                                            it.copy(selected = ids)
                                        }
                                    },
                                )
                            }
                            CommunityPerson(person)
                        }
                    }
                }
                YappButton(
                    stringResource(R.string.community_confirm),
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                )
            }
        }
    }
}

@Composable
internal fun SettlementEditorPhotos(
    state: SettlementEditorState,
    selectedUris: List<String>,
    onPick: () -> Unit,
    onRemoveExisting: (Long) -> Unit,
    onRemoveNew: (String) -> Unit,
) {
    val editable = !state.saving && !state.blocked
    val count = state.photos.size + selectedUris.size
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CommunityTitle(stringResource(R.string.community_proofs))
        YappButton(
            text = stringResource(R.string.community_add_photo, count, SettlementDraft.MAX_PHOTOS),
            onClick = onPick,
            enabled = editable && count < SettlementDraft.MAX_PHOTOS,
            variant = YappButtonVariant.OutlinedAssistive,
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            state.photos.forEach { photo ->
                EditablePhoto(photo.activeUrl(Instant.now()), editable) { onRemoveExisting(photo.id) }
            }
            selectedUris.forEach { uri -> EditablePhoto(Uri.parse(uri), editable) { onRemoveNew(uri) } }
        }
        CommunityCaption(stringResource(R.string.community_photo_guide))
    }
}

@Composable
private fun EditablePhoto(
    data: Any?,
    enabled: Boolean,
    onRemove: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (data != null) {
            ProtectedPhoto(data, Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)))
        }
        WithoutRipple {
            TextButton(onClick = onRemove, enabled = enabled) {
                Text(stringResource(R.string.community_remove_photo))
            }
        }
    }
}

@Composable
internal fun SettlementAccountFields(
    state: SettlementEditorState,
    enabled: Boolean,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    var selectingBank by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CommunityTitle(stringResource(R.string.community_bank_account))
        YappButton(
            text = state.bank?.let { bankLabel(it) } ?: stringResource(R.string.community_bank),
            onClick = { selectingBank = true },
            enabled = enabled,
            variant = YappButtonVariant.OutlinedAssistive,
        )
        DropdownMenu(expanded = selectingBank, onDismissRequest = { selectingBank = false }) {
            SettlementBank.entries.forEach { bank ->
                WithoutRipple {
                    DropdownMenuItem(
                        text = { Text(bankLabel(bank)) },
                        onClick = {
                            onEdit { it.copy(bank = bank) }
                            selectingBank = false
                        },
                    )
                }
            }
        }
        SettlementInput(state.number, stringResource(R.string.community_account), enabled) { value ->
            onEdit {
                it.copy(
                    number =
                        value
                            .filter { character ->
                                character in '0'..'9' || character == '-'
                            }.take(50),
                )
            }
        }
        SettlementInput(state.holder, stringResource(R.string.community_holder), enabled) { value ->
            onEdit { it.copy(holder = value) }
        }
        CommunityCaption(stringResource(R.string.community_account_guide))
    }
}

@Composable
private fun SettlementInput(
    value: String,
    label: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
    )
}
