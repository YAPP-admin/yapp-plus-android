package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.yapp.plus.core.designsystem.modifier.shimmerEffect
import com.yapp.plus.domain.notice.NoticeImage

@Composable
internal fun BoardNoticeImage(image: NoticeImage) {
    SubcomposeAsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(image.url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build(),
        contentDescription = image.filename,
        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
        contentScale = ContentScale.FillWidth,
        loading = { Box(Modifier.fillMaxWidth().heightIn(min = 100.dp).shimmerEffect()) },
        error = { BoardNoticeError() },
    )
}
