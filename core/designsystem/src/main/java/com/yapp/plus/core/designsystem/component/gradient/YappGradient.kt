package com.yapp.plus.core.designsystem.component.gradient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun YappBackgroundGradient(
    modifier: Modifier = Modifier,
    surface: YappBackgroundGradientSurface = YappBackgroundGradientSurface.Normal,
    alternative: Boolean = false,
    size: Dp = 40.dp
) {
    val color = remember(surface, alternative) {
        when (surface) {
            YappBackgroundGradientSurface.Normal,
            YappBackgroundGradientSurface.Elevated -> {
                if (alternative) Color(0xFFF7F7F8) else Color.White
            }
        }
    }
    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = remember(color) { verticalFadeBrush(color) }
            )
    )
}

@Composable
fun YappStaticGradient(
    modifier: Modifier = Modifier,
    color: YappStaticGradientColor = YappStaticGradientColor.Black,
    emphasis: YappStaticGradientEmphasis = YappStaticGradientEmphasis.Emphasis,
    size: Dp = 40.dp
) {
    val baseColor = when (color) {
        YappStaticGradientColor.White -> Color.White
        YappStaticGradientColor.Black -> Color.Black
    }
    val alpha = when (emphasis) {
        YappStaticGradientEmphasis.Light -> 0.12f
        YappStaticGradientEmphasis.Normal -> 0.22f
        YappStaticGradientEmphasis.Emphasis -> 1f
    }
    val gradientColor = baseColor.copy(alpha = alpha)

    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = remember(gradientColor) { verticalFadeBrush(gradientColor) }
            )
    )
}

private fun verticalFadeBrush(color: Color): Brush = Brush.verticalGradient(
    colors = listOf(color.copy(alpha = 0f), color)
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun YappGradientPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Background")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientSample("Normal") {
                YappBackgroundGradient()
            }
            GradientSample("Alternative") {
                YappBackgroundGradient(alternative = true)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("Static")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientSample("Black") {
                YappStaticGradient()
            }
            GradientSample("White") {
                Box(Modifier.background(Color(0xFF171719))) {
                    YappStaticGradient(color = YappStaticGradientColor.White)
                }
            }
        }
    }
}

@Composable
private fun GradientSample(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F2)),
            content = { content() }
        )
        Text(label)
    }
}
