package com.yapp.plus.feature.login

import android.app.Activity

internal interface GoogleLoginCredentialProvider {
    suspend fun getGoogleLoginCredential(
        activity: Activity,
        serverClientId: String,
    ): GoogleLoginCredential
}
