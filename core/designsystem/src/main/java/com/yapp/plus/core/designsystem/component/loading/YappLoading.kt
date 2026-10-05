package com.yapp.plus.core.designsystem.component.loading

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R

@Composable
fun YappLoading(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 80.dp
) {
    val resources = LocalResources.current
    val drawable = remember(resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(resources, R.drawable.yapp_loading_wanted)
            )
        } else {
            BitmapDrawable(
                resources,
                BitmapFactory.decodeResource(resources, R.drawable.yapp_loading_wanted)
            )
        }
    }

    DisposableEffect(drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            (drawable as? AnimatedImageDrawable)?.start()
        }
        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                (drawable as? AnimatedImageDrawable)?.stop()
            }
        }
    }

    AndroidView(
        modifier = modifier.size(size),
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                this.contentDescription = contentDescription
                setImageDrawable(drawable)
            }
        },
        update = { imageView ->
            imageView.contentDescription = contentDescription
            if (imageView.drawable !== drawable) imageView.setImageDrawable(drawable)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun YappLoadingPreview() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
        YappLoading()
    }
}
