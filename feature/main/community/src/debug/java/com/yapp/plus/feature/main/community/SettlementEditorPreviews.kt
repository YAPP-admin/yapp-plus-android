package com.yapp.plus.feature.main.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.SettlementBank

private val person = AfterpartyPerson(2, "박야뿌", "1팀", null, false)
private val editor =
    SettlementEditorState(
        candidates = listOf(person),
        selected = setOf(person.recipientId),
        bank = SettlementBank.TOSS,
        totalText = "72000",
        loading = false,
    )

@Preview(showBackground = true)
@Composable
fun SettlementEditorPreview() =
    YappTheme {
        SettlementEditorScreen(editor, false, emptyList(), {}, {}, {}, {}, {}, {})
    }

@Preview(showBackground = true)
@Composable
private fun SettlementAccountFieldsPreview() = YappTheme { SettlementAccountFields(editor, true) {} }

@Preview(showBackground = true)
@Composable
private fun SettlementEditorPhotosPreview() = YappTheme { SettlementEditorPhotos(editor, emptyList(), {}, {}, {}) }
