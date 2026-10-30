package com.yapp.plus.feature.main.board.components

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.yapp.plus.core.designsystem.theme.YappMainColor
import io.noties.markwon.Markwon
import java.net.URI
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun BoardMarkdown(markdown: String) {
    val context = LocalContext.current
    val renderer = remember(context) { Markwon.create(context) }
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = {
            TextView(it).apply {
                textSize = 14f
                setTextColor(YappMainColor.textPrimary.toArgb())
                setLinkTextColor(YappMainColor.textSecondary.toArgb())
                typeface = ResourcesCompat.getFont(it, DesignSystemR.font.pretendard_jp_medium)
                includeFontPadding = false
                setLineSpacing(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        5f,
                        resources.displayMetrics,
                    ),
                    1f,
                )
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { view ->
            val text = SpannableString(renderer.toMarkdown(markdown))
            text.getSpans(0, text.length, URLSpan::class.java).forEach { link ->
                val scheme = runCatching { URI(link.url).scheme }.getOrNull()
                if (scheme !in setOf("http", "https")) text.removeSpan(link)
            }
            view.text = text
        },
    )
}
