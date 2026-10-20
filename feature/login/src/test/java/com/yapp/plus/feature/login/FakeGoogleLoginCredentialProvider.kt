package com.yapp.plus.feature.login

import android.app.Activity
import com.yapp.plus.domain.auth.GoogleLoginCredential

internal class FakeGoogleLoginCredentialProvider : GoogleLoginCredentialProvider {
    var request: suspend () -> GoogleLoginCredential = {
        GoogleLoginCredential("fake-id-token", "raw-nonce")
    }
    var requestCount = 0

    override suspend fun getGoogleLoginCredential(
        activity: Activity?,
        serverClientId: String,
    ): GoogleLoginCredential {
        requestCount += 1
        return request()
    }
}
