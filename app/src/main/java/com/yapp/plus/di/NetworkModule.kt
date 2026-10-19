package com.yapp.plus.di

import com.yapp.plus.core.network.AccessTokenProvider
import com.yapp.plus.core.network.ApiHttpClientFactory
import com.yapp.plus.core.network.MutableAccessTokenProvider
import com.yapp.plus.data.session.SessionScheduleRepositoryImpl
import com.yapp.plus.domain.session.SessionScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideMutableAccessTokenProvider(): MutableAccessTokenProvider = MutableAccessTokenProvider()

    @Provides
    @Singleton
    fun provideAccessTokenProvider(provider: MutableAccessTokenProvider): AccessTokenProvider = provider

    @Provides
    @Singleton
    fun provideApiHttpClient(accessTokenProvider: AccessTokenProvider): HttpClient =
        ApiHttpClientFactory.create(accessTokenProvider)

    @Provides
    @Singleton
    fun provideSessionScheduleRepository(httpClient: HttpClient): SessionScheduleRepository =
        SessionScheduleRepositoryImpl(httpClient)
}
