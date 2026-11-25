package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementPhoto
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.feature.main.community.components.SettlementAccountFields
import com.yapp.plus.feature.main.community.components.SettlementBankField
import com.yapp.plus.feature.main.community.components.SettlementBankSheet
import com.yapp.plus.feature.main.community.components.SettlementCandidateContent
import com.yapp.plus.feature.main.community.components.SettlementCandidateFilters
import com.yapp.plus.feature.main.community.components.SettlementCandidateRow
import com.yapp.plus.feature.main.community.components.SettlementEditorAction
import com.yapp.plus.feature.main.community.components.SettlementEditorPhotos
import com.yapp.plus.feature.main.community.components.SettlementPerPersonField
import com.yapp.plus.feature.main.community.components.SettlementPhotoTile
import com.yapp.plus.feature.main.community.components.SettlementPhotoTrigger
import com.yapp.plus.feature.main.community.components.SettlementRosterFilter
import com.yapp.plus.feature.main.community.components.SettlementRosterSummary
import com.yapp.plus.feature.main.community.components.SettlementSplitSummary
import com.yapp.plus.feature.main.community.components.SettlementTotalField

private val designer = AfterpartyPerson(2, "최야뿌", "2팀", UserPosition.DESIGN, false)
private val planner = AfterpartyPerson(3, "박야뿌", "1팀", UserPosition.PM, false)
private val editor =
    SettlementEditorState(
        candidates = listOf(designer, planner),
        selected = setOf(designer.recipientId, planner.recipientId),
        bank = SettlementBank.TOSS,
        number = "1000-1234-5678",
        holder = "이야뿌",
        totalText = "102000",
        photos = listOf(SettlementPhoto(1, null, null), SettlementPhoto(2, null, null)),
        loading = false,
    )
private val emptyEditor =
    SettlementEditorState(candidates = editor.candidates, selected = editor.selected, loading = false)

@Preview(showBackground = true, widthDp = 393, heightDp = 1024)
@Composable
fun SettlementEditorPreview() =
    YappTheme {
        Box(Modifier.systemBarsPadding()) {
            SettlementEditorScreen(editor, false, emptyList(), {}, {}, {}, {}, {}, {})
        }
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 1024)
@Composable
fun EmptySettlementEditorPreview() =
    YappTheme {
        Box(Modifier.systemBarsPadding()) {
            SettlementEditorScreen(emptyEditor, false, emptyList(), {}, {}, {}, {}, {}, {})
        }
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 1024)
@Composable
private fun EditSettlementEditorPreview() =
    YappTheme { SettlementEditorScreen(editor, true, emptyList(), {}, {}, {}, {}, {}, {}) }

@Preview(showBackground = true, widthDp = 361)
@Composable
private fun SettlementRosterSummaryPreview() =
    YappTheme { SettlementRosterSummary(editor, isEmptyForm = false, enabled = true) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementAccountFieldsPreview() = YappTheme { SettlementAccountFields(editor, true) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementEditorPhotosPreview() = YappTheme { SettlementEditorPhotos(editor, emptyList(), {}, {}, {}) }

@Preview(showBackground = true, widthDp = 361)
@Composable
private fun SettlementTotalFieldPreview() = YappTheme { SettlementTotalField(emptyEditor, true) {} }

@Preview(showBackground = true, widthDp = 361)
@Composable
private fun SettlementPerPersonFieldPreview() = YappTheme { SettlementPerPersonField(editor, true) {} }

@Preview(showBackground = true, widthDp = 361)
@Composable
private fun SettlementSplitSummaryPreview() =
    YappTheme { SettlementSplitSummary(editor.copy(totalText = "102001"), showsDecimal = true) }

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SettlementCandidateContentPreview() = YappTheme { SettlementCandidateContent(editor, {}, {}) }

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun SettlementCandidateFiltersPreview() =
    YappTheme { SettlementCandidateFilters("", SettlementRosterFilter.All, 3, 2, {}, {}) }

@Preview(showBackground = true, widthDp = 361)
@Composable
private fun SettlementCandidateRowPreview() =
    YappTheme {
        SettlementCandidateRow(designer.name, designer.teamName, designer.position, true, false) {}
    }

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun SettlementBankSheetPreview() = YappTheme { SettlementBankSheet({}, {}) }

@Preview(showBackground = true, widthDp = 140)
@Composable
private fun SettlementBankFieldPreview() = YappTheme { SettlementBankField(SettlementBank.TOSS, true) {} }

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun SettlementEditorActionPreview() =
    YappTheme {
        Column {
            SettlementEditorAction("정산 요청하기 (2명)", enabled = true) {}
            SettlementEditorAction("정산 요청하기 (2명)", enabled = false) {}
        }
    }

@Preview(showBackground = true)
@Composable
private fun SettlementPhotoTriggerPreview() = YappTheme { SettlementPhotoTrigger(0, true) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementPhotoTilePreview() {
    YappTheme { SettlementPhotoTile(null, isCover = true, editable = true) {} }
}
