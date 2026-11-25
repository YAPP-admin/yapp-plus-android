package com.yapp.plus.feature.main.home.team

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@Composable
internal fun rememberHomeTeamPhotoPicker(viewModel: HomeTeamAttendanceViewModel): (Boolean) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val capture = File(context.cacheDir, "team-attendance-camera/capture.jpg")
    val deliver: (Uri?, Boolean) -> Unit = { uri, fromCamera ->
        scope.launch {
            val photo =
                withContext(Dispatchers.IO) {
                    try {
                        uri?.let { loadTeamPhoto(context, it) }
                    } finally {
                        if (fromCamera) capture.delete()
                    }
                }
            viewModel.photoPicked(photo, invalid = uri != null && photo == null)
        }
    }
    val gallery =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            deliver(it, false)
        }
    val camera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            deliver(Uri.fromFile(capture).takeIf { success }, true)
        }
    return pick@{ takePhoto ->
        if (!viewModel.preparePhoto()) return@pick
        try {
            if (takePhoto) {
                capture.parentFile?.mkdirs()
                capture.delete()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.team-attendance-camera", capture)
                camera.launch(uri)
            } else {
                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } catch (_: Exception) {
            if (takePhoto) capture.delete()
            viewModel.photoPicked(null, invalid = true)
        }
    }
}

private fun loadTeamPhoto(
    context: Context,
    uri: Uri,
): HomeTeamPhotoInput? =
    runCatching {
        val bytes = context.contentResolver.openInputStream(uri)?.use(::readBounded) ?: return null
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        val mime = options.outMimeType
        if (mime != "image/jpeg" && mime != "image/png") return null
        options.inJustDecodeBounds = false
        while (maxOf(options.outWidth, options.outHeight) / options.inSampleSize.coerceAtLeast(1) > PREVIEW_EDGE) {
            options.inSampleSize = options.inSampleSize.coerceAtLeast(1) * 2
        }
        val preview = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return null
        HomeTeamPhotoInput(bytes, mime, preview)
    }.getOrNull()

private fun readBounded(stream: InputStream): ByteArray? {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        val count = stream.read(buffer)
        if (count < 0) break
        if (output.size() + count > MAX_PHOTO_BYTES) return null
        output.write(buffer, 0, count)
    }
    return output.toByteArray().takeIf { it.isNotEmpty() }
}

private const val MAX_PHOTO_BYTES = 10 * 1024 * 1024
private const val PREVIEW_EDGE = 1600
