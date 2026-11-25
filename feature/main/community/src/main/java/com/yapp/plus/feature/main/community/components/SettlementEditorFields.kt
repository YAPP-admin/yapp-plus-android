package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.input.YappTextField
import com.yapp.plus.core.designsystem.components.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.modifier.noRippleToggleable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.feature.main.community.R
import com.yapp.plus.feature.main.community.SettlementEditorState
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

private val summaryAvatars =
    listOf(
        R.drawable.settlement_avatar_yellow,
        R.drawable.settlement_avatar_purple,
        R.drawable.settlement_avatar_orange,
    )

@Composable
internal fun SettlementRosterSummary(
    state: SettlementEditorState,
    isEmptyForm: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val selectedNames =
        state.candidates
            .filter { it.recipientId in state.selected }
            .take(2)
            .map { it.name }
    val names = (listOf(stringResource(R.string.community_myself)) + selectedNames).joinToString(", ")
    val remaining = state.selected.size - selectedNames.size
    val summary = if (remaining > 0) stringResource(R.string.community_selected_names, names, remaining) else names
    YappClickableSurface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = YappMainColor.basement,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                YappText(
                    text =
                        stringResource(
                            if (isEmptyForm) R.string.community_roster_count else R.string.community_selected_count,
                            state.peopleCount,
                        ),
                    style = YappTypography.scaleT5Bold,
                    color = YappMainColor.textPrimary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                        summaryAvatars.take(state.peopleCount).forEachIndexed { index, resource ->
                            Image(
                                painter = painterResource(resource),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp).zIndex((summaryAvatars.size - index).toFloat()),
                            )
                        }
                    }
                    YappText(
                        text = summary,
                        style = YappTypography.scaleT4Regular,
                        color = YappMainColor.textSubtle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.settlement_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
internal fun SettlementTotalField(
    state: SettlementEditorState,
    enabled: Boolean,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettlementFormHeading(R.string.community_total_amount)
        SettlementFormInput(
            value = state.totalText,
            label = stringResource(R.string.community_total_amount),
            placeholder = stringResource(R.string.community_total_amount_placeholder),
            enabled = enabled,
            isAmount = true,
            showsWon = state.totalText.isNotEmpty(),
            keyboardType = KeyboardType.Number,
            onChange = { value -> onEdit { it.copy(totalText = value.filter(Char::isDigit)) } },
        )
    }
}

@Composable
internal fun SettlementPerPersonField(
    state: SettlementEditorState,
    enabled: Boolean,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    var showsDecimal by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettlementFormHeading(R.string.community_per_person)
        SettlementFormInput(
            value = state.amountText,
            label = stringResource(R.string.community_per_person),
            placeholder = "",
            enabled = enabled,
            isAmount = true,
            showsWon = true,
            keyboardType = KeyboardType.Number,
            onChange = { value -> onEdit { it.copy(amountOverride = value.filter(Char::isDigit)) } },
        )
        SettlementSplitSummary(state, showsDecimal)
        Row(
            modifier =
                Modifier.noRippleToggleable(
                    value = showsDecimal,
                    enabled = true,
                    role = Role.Checkbox,
                    onValueChange = { showsDecimal = it },
                ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettlementCheckIcon(showsDecimal)
            YappText(
                text = stringResource(R.string.community_show_decimal),
                style = YappTypography.scaleT3Medium,
                color = YappMainColor.textSubtle,
            )
        }
    }
}

@Composable
internal fun SettlementSplitSummary(
    state: SettlementEditorState,
    showsDecimal: Boolean,
) {
    val total = state.totalText.toLongOrNull()
    val formula =
        if (total == null) {
            stringResource(R.string.community_split_empty)
        } else {
            stringResource(R.string.community_split_formula, money(total), state.peopleCount)
        }
    val amount =
        if (showsDecimal) {
            val formatted =
                NumberFormat.getNumberInstance(Locale.KOREA).apply {
                    maximumFractionDigits = 2
                }
            val divided =
                BigDecimal
                    .valueOf(total ?: 0)
                    .divide(BigDecimal.valueOf(state.peopleCount.toLong()), 2, RoundingMode.HALF_UP)
            stringResource(R.string.community_money, formatted.format(divided))
        } else {
            money((total ?: 0) / state.peopleCount)
        }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .background(YappMainColor.basement, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(formula, YappTypography.scaleT4Regular, YappMainColor.textSubtle, Modifier.weight(1f))
        YappText(amount, YappTypography.scaleT6Bold, YappMainColor.textPrimary)
    }
}

@Composable
internal fun SettlementAccountFields(
    state: SettlementEditorState,
    enabled: Boolean,
    onEdit: ((SettlementEditorState) -> SettlementEditorState) -> Unit,
) {
    var selectingBank by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettlementFormHeading(R.string.community_bank_account)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SettlementBankField(
                bank = state.bank,
                enabled = enabled,
                modifier = Modifier.weight(140f),
                onClick = {
                    focusManager.clearFocus()
                    selectingBank = true
                },
            )
            SettlementFormInput(
                value = state.number,
                label = stringResource(R.string.community_account),
                enabled = enabled,
                modifier = Modifier.weight(213f),
                keyboardType = KeyboardType.Phone,
                onChange = { value ->
                    onEdit { it.copy(number = value.filter { char -> char in '0'..'9' || char == '-' }.take(50)) }
                },
            )
        }
        SettlementFormInput(
            value = state.holder,
            label = stringResource(R.string.community_holder),
            enabled = enabled,
            onChange = { value -> onEdit { it.copy(holder = value) } },
        )
        SettlementFormGuide(R.string.community_account_guide)
    }
    if (selectingBank && enabled) {
        SettlementBankSheet(
            onClose = { selectingBank = false },
            onSelect = { bank ->
                onEdit { it.copy(bank = bank) }
                selectingBank = false
            },
        )
    }
}

@Composable
internal fun SettlementBankField(
    bank: SettlementBank?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    YappClickableSurface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = YappMainColor.surface,
        border = BorderStroke(1.dp, YappMainColor.border),
        modifier = modifier,
    ) {
        Row(
            Modifier.heightIn(min = 52.dp).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            YappText(
                text = bank?.let { editorBankLabel(it) } ?: stringResource(R.string.community_bank),
                style = YappTypography.scaleT5Regular,
                color = if (bank == null) YappMainColor.textFaint else YappMainColor.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Image(
                painter = painterResource(R.drawable.settlement_chevron_down),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

private val preferredBanks =
    listOf(
        SettlementBank.TOSS,
        SettlementBank.KAKAO,
        SettlementBank.KB_KOOKMIN,
        SettlementBank.SHINHAN,
        SettlementBank.WOORI,
        SettlementBank.HANA,
        SettlementBank.NH,
        SettlementBank.IBK,
        SettlementBank.K_BANK,
        SettlementBank.MG,
    )
private val availableBanks = preferredBanks + SettlementBank.entries.filterNot { it in preferredBanks }

@Composable
internal fun SettlementBankSheet(
    onClose: () -> Unit,
    onSelect: (SettlementBank) -> Unit,
) {
    YappBottomSheet(
        onDismissRequest = onClose,
        onConfirm = {},
        onDismiss = onClose,
        showHandle = true,
        title = null,
        body = null,
        confirmText = null,
        dismissText = null,
        variant = YappBottomSheetVariant.Home,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            YappText(stringResource(R.string.community_bank), YappTypography.scaleT7Bold, YappMainColor.textPrimary)
            LazyColumn(Modifier.heightIn(max = 520.dp)) {
                items(availableBanks) { bank ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .noRippleClickable { onSelect(bank) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        YappText(editorBankLabel(bank), YappTypography.scaleT5Regular, YappMainColor.textPrimary)
                    }
                }
            }
        }
    }
}

@Composable
internal fun editorBankLabel(bank: SettlementBank): String {
    val index = preferredBanks.indexOf(bank)
    return if (index >= 0) stringArrayResource(R.array.community_editor_banks)[index] else bankLabel(bank)
}

@Composable
internal fun SettlementFormInput(
    value: String,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = label,
    isAmount: Boolean = false,
    showsWon: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
) {
    val currency: (@Composable () -> Unit)? =
        if (showsWon) {
            {
                YappText(
                    stringResource(R.string.community_won_unit),
                    YappTypography.scaleT5Regular,
                    YappMainColor.textSubtle,
                )
            }
        } else {
            null
        }
    YappTextField(
        value = value,
        onValueChange = onChange,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        variant = YappTextFieldVariant.Form,
        inputContentDescription = label,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = currency,
        visualTransformation = if (isAmount) SettlementAmountGrouping else VisualTransformation.None,
    )
}

internal val SettlementAmountGrouping =
    VisualTransformation { original ->
        val grouped =
            original.text
                .reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()
        val digitPositions = grouped.indices.filter { grouped[it] != ',' }
        val offsets =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset == 0) return 0
                    return digitPositions[offset - 1] + 1
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val textBeforeCursor = grouped.take(offset)
                    return textBeforeCursor.count { it != ',' }
                }
            }
        TransformedText(AnnotatedString(grouped), offsets)
    }

@Composable
internal fun SettlementFormHeading(
    label: Int,
    optional: Boolean = false,
) {
    Row(
        Modifier.padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(stringResource(label), YappTypography.scaleT5Bold, YappMainColor.textPrimary)
        if (optional) {
            YappText(
                stringResource(R.string.community_optional),
                YappTypography.scaleT4Regular,
                YappMainColor.textSubtle,
            )
        }
    }
}

@Composable
internal fun SettlementFormGuide(label: Int) {
    YappText(
        text = stringResource(label),
        style = YappTypography.scaleT4Regular,
        color = YappMainColor.textSubtle,
        modifier = Modifier.padding(horizontal = 2.dp),
    )
}

@Composable
internal fun SettlementEditorAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column {
        HorizontalDivider(color = YappMainColor.divider)
        YappButton(
            text = text,
            onClick = onClick,
            variant = YappButtonVariant.BrandAction,
            size = YappButtonSize.MainAction,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}
