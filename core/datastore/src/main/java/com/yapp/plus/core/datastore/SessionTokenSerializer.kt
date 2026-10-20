package com.yapp.plus.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException

internal class SessionTokenSerializer : Serializer<SessionTokenRecord> {
    private val cipher = SessionTokenCipher()
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: SessionTokenRecord = SessionTokenRecord()

    override suspend fun readFrom(input: InputStream): SessionTokenRecord {
        val encrypted = input.readBytes()
        return try {
            json.decodeFromString<SessionTokenRecord>(cipher.decrypt(encrypted).decodeToString())
        } catch (_: GeneralSecurityException) {
            throw CorruptionException("Stored session cannot be decrypted")
        } catch (_: SerializationException) {
            throw CorruptionException("Stored session cannot be decoded")
        } catch (_: IllegalArgumentException) {
            throw CorruptionException("Stored session is invalid")
        }
    }

    override suspend fun writeTo(
        t: SessionTokenRecord,
        output: OutputStream,
    ) {
        val encoded = json.encodeToString(t).encodeToByteArray()
        output.write(cipher.encrypt(encoded))
    }
}
