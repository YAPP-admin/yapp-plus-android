package com.yapp.plus.feature.login

import android.app.Activity
import com.yapp.plus.feature.login.model.GoogleLoginCredential

internal interface GoogleLoginCredentialProvider {
    suspend fun getGoogleLoginCredential(
        activity: Activity,
        serverClientId: String,
    ): GoogleLoginCredential
}
