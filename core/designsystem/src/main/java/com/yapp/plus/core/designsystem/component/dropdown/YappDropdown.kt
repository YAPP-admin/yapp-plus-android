package com.yapp.plus.core.designsystem.component.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappDropdown(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "선택해주세요",
    size: YappDropdownSize = YappDropdownSize.Large,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val metrics = dropdownMetrics(size)
    Column(modifier = modifier) {
        YappText(title, style = YappTypography.label1NormalMedium, color = LabelColor)
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().height(metrics.height),
                enabled = enabled,
                shape = RoundedCornerShape(metrics.cornerRadius),
                color = Color.White,
                contentColor = LabelColor,
                border = BorderStroke(
                    1.dp,
                    when {
                        expanded -> PrimaryColor
                        selectedOption != null -> StrongBorderColor
                        else -> NeutralBorderColor
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = metrics.horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    YappText(
                        text = selectedOption ?: placeholder,
                        modifier = Modifier.weight(1f),
                        style = YappTypography.body1NormalRegular,
                        color = if (selectedOption == null) PlaceholderColor else LabelColor
                    )
                    Image(
                        painter = painterResource(
                            if (size == YappDropdownSize.Large) {
                                if (expanded) R.drawable.ic_dropdown_caret_up_large
                                else R.drawable.ic_dropdown_caret_down_large
                            } else if (expanded) {
                                R.drawable.ic_dropdown_caret_up_medium
                            } else {
                                R.drawable.ic_dropdown_caret_down_medium
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(metrics.iconSize)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.widthIn(min = 320.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                tonalElevation = 0.dp,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, NeutralBorderColor)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            YappText(option, style = YappTypography.body1NormalRegular, color = LabelColor)
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        modifier = Modifier
                            .width(320.dp)
                            .height(metrics.menuItemHeight)
                            .background(if (option == selectedOption) SelectedColor else Color.Transparent),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

private fun dropdownMetrics(size: YappDropdownSize) = when (size) {
    YappDropdownSize.Large -> DropdownMetrics(48.dp, 16.dp, 10.dp, 24.dp, 48.dp)
    YappDropdownSize.Medium -> DropdownMetrics(40.dp, 10.dp, 8.dp, 20.dp, 40.dp)
}

private val LabelColor = Color(0xFF171719)
private val PlaceholderColor = Color(0x4737383C)
private val PrimaryColor = Color(0xFFFA6027)
private val NeutralBorderColor = Color(0x3870737C)
private val StrongBorderColor = Color(0x8570737C)
private val SelectedColor = Color(0x1470737C)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappDropdownPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                YappDropdown(
                    title = "참석 여부",
                    options = listOf("참석", "불참", "미정"),
                    selectedOption = null,
                    onOptionSelected = {}
                )
                YappDropdown(
                    title = "참석 여부",
                    options = listOf("참석", "불참"),
                    selectedOption = "참석",
                    onOptionSelected = {},
                    size = YappDropdownSize.Medium
                )
            }
        }
    }
}
