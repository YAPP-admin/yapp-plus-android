package com.yapp.plus.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

internal object NotificationPreferenceSerializer : Serializer<Boolean> {
    override val defaultValue: Boolean = true

    override suspend fun readFrom(input: InputStream): Boolean =
        when (input.readBytes().decodeToString()) {
            "true" -> true
            "false" -> false
            else -> throw CorruptionException("Invalid notification preference")
        }

    override suspend fun writeTo(
        t: Boolean,
        output: OutputStream,
    ) {
        output.write(t.toString().encodeToByteArray())
    }
}
