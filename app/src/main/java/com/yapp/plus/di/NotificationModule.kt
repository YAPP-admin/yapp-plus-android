package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.notification.NotificationPreferenceDataSource
import com.yapp.plus.data.notification.NotificationPreferenceLocalDataSource
import com.yapp.plus.data.notification.NotificationPreferenceRepositoryImpl
import com.yapp.plus.domain.notification.NotificationDeviceRepository
import com.yapp.plus.domain.notification.NotificationPreferenceRepository
import com.yapp.plus.notification.FirebasePushEnvironment
import com.yapp.plus.notification.PushEnvironment
import com.yapp.plus.notification.PushRegistration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NotificationModule {
    @Provides
    @Singleton
    fun registration(
        environment: PushEnvironment,
        tokenStore: TokenStore,
        repository: NotificationDeviceRepository,
        preferences: NotificationPreferenceRepository,
    ): PushRegistration = PushRegistration(environment, tokenStore, repository, preferences)

    @Provides
    fun environment(environment: FirebasePushEnvironment): PushEnvironment = environment

    @Provides
    @Singleton
    fun preferenceSource(
        @ApplicationContext context: Context,
    ): NotificationPreferenceDataSource = NotificationPreferenceLocalDataSource(context)

    @Provides
    @Singleton
    fun preferences(source: NotificationPreferenceDataSource): NotificationPreferenceRepository =
        NotificationPreferenceRepositoryImpl(source)
}
