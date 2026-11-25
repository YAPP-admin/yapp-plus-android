package com.yapp.plus.feature.main.community

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import com.yapp.plus.domain.afterparty.SettlementDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

internal suspend fun readSettlementPhoto(
    resolver: ContentResolver,
    uri: Uri,
): SettlementPhotoInput? =
    withContext(Dispatchers.IO) {
        val bytes =
            try {
                resolver.openInputStream(uri)?.use(::readPhotoBytes)
            } catch (_: IOException) {
                null
            } catch (_: SecurityException) {
                null
            } ?: return@withContext null
        val metadata = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, metadata)
        val type = metadata.outMimeType ?: return@withContext null
        if (type !in photoTypes || metadata.outWidth <= 0 || metadata.outHeight <= 0) return@withContext null
        SettlementPhotoInput(bytes, type)
    }

private fun readPhotoBytes(stream: InputStream): ByteArray? {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        val count = stream.read(buffer)
        if (count < 0) break
        if (output.size() + count > SettlementDraft.MAX_PHOTO_BYTES) return null
        output.write(buffer, 0, count)
    }
    return output.toByteArray().takeIf { it.isNotEmpty() }
}

private val photoTypes = setOf("image/jpeg", "image/png")
