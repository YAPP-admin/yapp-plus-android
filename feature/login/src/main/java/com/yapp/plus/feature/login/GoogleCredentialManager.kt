package com.yapp.plus.feature.login

import android.app.Activity
import android.content.MutableContextWrapper
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.yapp.plus.domain.auth.GoogleLoginCredential
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject

internal class GoogleCredentialManager
    @Inject
    constructor(
        private val credentialManager: CredentialManager,
    ) : GoogleLoginCredentialProvider {
        override suspend fun getGoogleLoginCredential(
            activity: Activity?,
            serverClientId: String,
        ): GoogleLoginCredential {
            val nonce = generateNonce()
            val request = createGoogleCredentialRequest(serverClientId, hashNonce(nonce))
            val credential = requestCredential(checkNotNull(activity), request)
            return GoogleLoginCredential(
                idToken = parseGoogleIdToken(credential),
                rawNonce = nonce,
            )
        }

        private fun createGoogleCredentialRequest(
            serverClientId: String,
            nonce: String,
        ): GetCredentialRequest {
            val googleOption =
                GetSignInWithGoogleOption
                    .Builder(serverClientId)
                    .setNonce(nonce)
                    .build()
            return GetCredentialRequest
                .Builder()
                .addCredentialOption(googleOption)
                .build()
        }

        private suspend fun requestCredential(
            activity: Activity,
            request: GetCredentialRequest,
        ): Credential =
            credentialManager
                .getCredential(
                    context = MutableContextWrapper(activity),
                    request = request,
                ).credential

        private fun parseGoogleIdToken(credential: Credential): String {
            val googleCredential = credential.requireGoogleIdTokenCredential()
            return try {
                GoogleIdTokenCredential.createFrom(googleCredential.data).idToken
            } catch (exception: GoogleIdTokenParsingException) {
                throw IllegalStateException(
                    "Google ID token response could not be parsed.",
                    exception,
                )
            }
        }

        private fun Credential.requireGoogleIdTokenCredential(): CustomCredential {
            check(
                this is CustomCredential &&
                    type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
            ) {
                "Credential Manager returned an unsupported credential type."
            }
            return this
        }

        internal companion object {
            private const val NONCE_BYTE_LENGTH = 32
            private val secureRandom = SecureRandom()

            fun generateNonce(): String {
                val bytes = ByteArray(NONCE_BYTE_LENGTH)
                secureRandom.nextBytes(bytes)
                return bytes.toHex()
            }

            fun hashNonce(rawNonce: String): String =
                MessageDigest.getInstance("SHA-256").digest(rawNonce.toByteArray()).toHex()

            private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
        }
    }
