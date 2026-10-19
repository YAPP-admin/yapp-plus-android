package com.yapp.plus.feature.login

import android.app.Activity
import android.content.MutableContextWrapper
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom

class GoogleCredentialManager(
    private val credentialManager: CredentialManager,
) {
    suspend fun getGoogleLoginCredential(
        activity: Activity,
        serverClientId: String,
    ): GoogleLoginCredential {
        val nonce = generateNonce()
        val googleOption =
            GetSignInWithGoogleOption
                .Builder(serverClientId)
                .setNonce(nonce)
                .build()
        val request =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleOption)
                .build()
        val response =
            credentialManager.getCredential(
                context = MutableContextWrapper(activity),
                request = request,
            )
        val credential = response.credential
        check(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
        ) {
            "Credential Manager returned an unsupported credential type."
        }
        val googleCredential =
            try {
                GoogleIdTokenCredential.createFrom(credential.data)
            } catch (exception: GoogleIdTokenParsingException) {
                throw IllegalStateException(
                    "Google ID token response could not be parsed.",
                    exception,
                )
            }
        return GoogleLoginCredential(
            idToken = googleCredential.idToken,
            nonce = nonce,
        )
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(NONCE_BYTE_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING,
        )
    }

    private companion object {
        const val NONCE_BYTE_LENGTH = 32
        val secureRandom = SecureRandom()
    }
}
