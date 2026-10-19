package com.yapp.plus.feature.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GoogleLoginCredentialProviderModule {
    @Binds
    abstract fun bindGoogleLoginCredentialProvider(
        googleCredentialManager: GoogleCredentialManager,
    ): GoogleLoginCredentialProvider
}
