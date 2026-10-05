package com.yapp.plus.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.theme.YappTheme

enum class YappButtonVariant {
    SolidPrimary,
    SolidSecondary,
    OutlinedPrimary,
    OutlinedSecondary,
    OutlinedAssistive
}

enum class YappButtonSize {
    XLarge,
    Large,
    Medium,
    Small,
    XSmall
}

@Composable
fun YappButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: YappButtonVariant = YappButtonVariant.SolidPrimary,
    size: YappButtonSize = YappButtonSize.Large,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val metrics = buttonMetrics(size)
    val colors = buttonColors(variant, enabled)

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(metrics.cornerRadius),
        color = colors.container,
        contentColor = colors.content,
        border = colors.border?.let { BorderStroke(1.dp, it) }
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = metrics.horizontalPadding,
                vertical = metrics.verticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(metrics.iconSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let { icon ->
                Box(
                    modifier = Modifier.size(metrics.iconSize),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }
            Text(
                text = text,
                style = buttonTextStyle(size),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            trailingIcon?.let { icon ->
                Box(
                    modifier = Modifier.size(metrics.iconSize),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }
        }
    }
}

@Composable
private fun buttonTextStyle(size: YappButtonSize) = when (size) {
    YappButtonSize.XLarge,
    YappButtonSize.Large -> MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.0912.sp,
        fontWeight = FontWeight.SemiBold
    )

    YappButtonSize.Medium -> MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.144.sp,
        fontWeight = FontWeight.SemiBold
    )

    YappButtonSize.Small,
    YappButtonSize.XSmall -> MaterialTheme.typography.labelMedium.copy(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2522.sp,
        fontWeight = FontWeight.SemiBold
    )
}

private fun buttonMetrics(size: YappButtonSize) = when (size) {
    YappButtonSize.XLarge -> ButtonMetrics(36.dp, 16.dp, 12.dp, 6.dp, 20.dp)
    YappButtonSize.Large -> ButtonMetrics(28.dp, 12.dp, 10.dp, 6.dp, 20.dp)
    YappButtonSize.Medium -> ButtonMetrics(20.dp, 9.dp, 8.dp, 5.dp, 18.dp)
    YappButtonSize.Small -> ButtonMetrics(14.dp, 7.dp, 6.dp, 4.dp, 16.dp)
    YappButtonSize.XSmall -> ButtonMetrics(12.dp, 5.dp, 4.dp, 4.dp, 16.dp)
}

private fun buttonColors(
    variant: YappButtonVariant,
    enabled: Boolean
) = when (variant) {
    YappButtonVariant.SolidPrimary -> ButtonColors(
        container = if (enabled) PrimaryColor else DisabledContainerColor,
        content = if (enabled) WhiteColor else DisabledPrimaryContentColor,
        border = null
    )

    YappButtonVariant.SolidSecondary -> ButtonColors(
        container = if (enabled) SecondaryContainerColor else DisabledSecondaryContainerColor,
        content = if (enabled) PrimaryColor else DisabledSecondaryContentColor,
        border = null
    )

    YappButtonVariant.OutlinedPrimary -> ButtonColors(
        container = Color.Transparent,
        content = if (enabled) PrimaryColor else DisabledOutlineContentColor,
        border = if (enabled) PrimaryColor else DisabledOutlineBorderColor
    )

    YappButtonVariant.OutlinedSecondary -> ButtonColors(
        container = Color.Transparent,
        content = if (enabled) PrimaryColor else DisabledOutlineContentColor,
        border = if (enabled) NeutralBorderColor else DisabledOutlineBorderColor
    )

    YappButtonVariant.OutlinedAssistive -> ButtonColors(
        container = Color.Transparent,
        content = if (enabled) LabelColor else DisabledOutlineContentColor,
        border = if (enabled) NeutralBorderColor else DisabledOutlineBorderColor
    )
}

private data class ButtonMetrics(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
    val iconSpacing: Dp,
    val iconSize: Dp
)

private data class ButtonColors(
    val container: Color,
    val content: Color,
    val border: Color?
)

private val PrimaryColor = Color(0xFFFA6027)
private val SecondaryContainerColor = Color(0xFFFFEFE9)
private val DisabledContainerColor = Color(0xFFF4F4F5)
private val DisabledSecondaryContainerColor = Color(0xFFFFF8F5)
private val WhiteColor = Color(0xFFFFFFFF)
private val LabelColor = Color(0xFF171719)
private val NeutralBorderColor = Color(0x3870737C)
private val DisabledOutlineBorderColor = Color(0x3870737C)
private val DisabledPrimaryContentColor = Color(0x4737383C)
private val DisabledSecondaryContentColor = Color(0xFFFDBBA2)
private val DisabledOutlineContentColor = Color(0x2937383C)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappButtonPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Primary", {}, variant = YappButtonVariant.SolidPrimary)
                    YappButton("Disabled", {}, enabled = false)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Secondary", {}, variant = YappButtonVariant.SolidSecondary)
                    YappButton(
                        "Disabled",
                        {},
                        variant = YappButtonVariant.SolidSecondary,
                        enabled = false
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Primary", {}, variant = YappButtonVariant.OutlinedPrimary)
                    YappButton(
                        "Disabled",
                        {},
                        variant = YappButtonVariant.OutlinedPrimary,
                        enabled = false
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Secondary", {}, variant = YappButtonVariant.OutlinedSecondary)
                    YappButton(
                        "Assistive",
                        {},
                        variant = YappButtonVariant.OutlinedAssistive
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    YappButton("XL", {}, size = YappButtonSize.XLarge)
                    YappButton("L", {}, size = YappButtonSize.Large)
                    YappButton("M", {}, size = YappButtonSize.Medium)
                    YappButton("S", {}, size = YappButtonSize.Small)
                    YappButton("XS", {}, size = YappButtonSize.XSmall)
                }
            }
        }
    }
}
