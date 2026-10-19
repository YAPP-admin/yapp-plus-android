package com.yapp.plus.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: YappButtonVariant = YappButtonVariant.SolidPrimary,
    size: YappButtonSize = YappButtonSize.Large,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val metrics = ButtonMetrics.from(size)
    val colors = ButtonColors.from(variant, enabled)

    Surface(
        onClick = onClick,
        modifier = modifier.alpha(colors.alpha),
        enabled = enabled,
        shape = RoundedCornerShape(metrics.cornerRadius),
        color = colors.container,
        contentColor = colors.content,
        border = colors.border?.let { BorderStroke(1.dp, it) },
    ) {
        Row(
            modifier =
                Modifier
                    .heightIn(min = metrics.minimumHeight)
                    .padding(
                        horizontal = metrics.horizontalPadding,
                        vertical = metrics.verticalPadding,
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    space = metrics.iconSpacing,
                    alignment = metrics.horizontalAlignment,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.let { icon ->
                Box(
                    modifier = Modifier.size(metrics.iconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
            Text(
                text = text,
                style = buttonTextStyle(size),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
            )
            trailingIcon?.let { icon ->
                Box(
                    modifier = Modifier.size(metrics.iconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
        }
    }
}

@Composable
private fun buttonTextStyle(size: YappButtonSize) =
    when (size) {
        YappButtonSize.CallToAction ->
            YappTypography.headline1Bold.copy(
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            )

        YappButtonSize.Social -> YappTypography.body1NormalBold.copy(letterSpacing = 0.sp)

        YappButtonSize.XLarge,
        YappButtonSize.Large,
        ->
            MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.0912.sp,
                fontWeight = FontWeight.SemiBold,
            )

        YappButtonSize.Medium ->
            MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.144.sp,
                fontWeight = FontWeight.SemiBold,
            )

        YappButtonSize.Small,
        YappButtonSize.XSmall,
        ->
            MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.2522.sp,
                fontWeight = FontWeight.SemiBold,
            )
    }

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappButtonPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        enabled = false,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Primary", {}, variant = YappButtonVariant.OutlinedPrimary)
                    YappButton(
                        "Disabled",
                        {},
                        variant = YappButtonVariant.OutlinedPrimary,
                        enabled = false,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappButton("Secondary", {}, variant = YappButtonVariant.OutlinedSecondary)
                    YappButton(
                        "Assistive",
                        {},
                        variant = YappButtonVariant.OutlinedAssistive,
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
