package com.yapp.plus.feature.auth

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.component.button.YappButton
import com.yapp.plus.core.designsystem.component.button.YappButtonSize
import com.yapp.plus.core.designsystem.component.button.YappButtonVariant
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTypography

/** System bar insets are supplied by the App's Scaffold. */
@Composable
internal fun AuthLayout(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bottom: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    BackHandler(onBack = onBack)
    Box(
        modifier = modifier.background(YappAuthColor.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(start = 4.dp),
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Image(
                        painter = painterResource(R.drawable.ic_auth_back),
                        contentDescription = stringResource(R.string.auth_back),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            Box(modifier = Modifier.weight(1f), content = content)
            bottom()
        }
    }
}

@Composable
internal fun AuthHeading(text: String) {
    YappText(
        text = text,
        modifier = Modifier.semantics { heading() },
        style =
            YappTypography.title3Bold.copy(
                fontSize = 26.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.5).sp,
            ),
        color = YappAuthColor.textPrimary,
    )
}

@Composable
internal fun AuthGraphic(
    @DrawableRes resource: Int,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
) {
    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier =
            modifier
                .widthIn(max = 350.dp)
                .fillMaxWidth()
                .height(height),
        contentScale = ContentScale.Fit,
    )
}

@Composable
internal fun AuthPrimaryAction(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
) {
    YappButton(
        text = text,
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        variant = YappButtonVariant.SolidBrand,
        size = YappButtonSize.CallToAction,
        enabled = isEnabled,
    )
}
