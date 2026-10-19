package com.yapp.plus.feature.login.di

import com.yapp.plus.feature.login.GoogleCredentialManager
import com.yapp.plus.feature.login.GoogleLoginCredentialProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoginCredentialBindingModule {
    @Binds
    abstract fun bindGoogleLoginCredentialProvider(
        googleCredentialManager: GoogleCredentialManager,
    ): GoogleLoginCredentialProvider
}
