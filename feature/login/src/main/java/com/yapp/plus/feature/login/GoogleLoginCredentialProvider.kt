package com.yapp.plus.feature.login

import android.app.Activity
import com.yapp.plus.domain.auth.GoogleLoginCredential

internal interface GoogleLoginCredentialProvider {
    suspend fun getGoogleLoginCredential(
        activity: Activity?,
        serverClientId: String,
    ): GoogleLoginCredential
}
