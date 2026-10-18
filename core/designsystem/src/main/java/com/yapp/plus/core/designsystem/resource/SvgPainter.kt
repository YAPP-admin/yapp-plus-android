package com.yapp.plus.core.designsystem.resource

import android.graphics.Picture
import android.graphics.RectF
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.caverock.androidsvg.SVG
import kotlin.math.roundToInt

@Composable
fun svgPainterResource(
    @RawRes resourceId: Int,
): Painter {
    val resources = LocalContext.current.resources
    return remember(resources, resourceId) {
        val svg = resources.openRawResource(resourceId).use { SVG.getFromInputStream(it) }
        val viewBox = svg.documentViewBox
        val width = svg.documentWidth.takeIf { it > 0f } ?: viewBox?.width() ?: 0f
        val height = svg.documentHeight.takeIf { it > 0f } ?: viewBox?.height() ?: 0f
        require(width > 0f && height > 0f) {
            "SVG resource $resourceId must declare its width and height or a viewBox."
        }

        SvgPainter(
            picture = svg.renderToPicture(width.roundToInt(), height.roundToInt()),
            intrinsicSize = Size(width, height),
        )
    }
}

private class SvgPainter(
    private val picture: Picture,
    override val intrinsicSize: Size,
) : Painter() {
    override fun DrawScope.onDraw() {
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawPicture(
                picture,
                RectF(0f, 0f, size.width, size.height),
            )
        }
    }
}
