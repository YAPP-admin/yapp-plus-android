package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.core.network.ApiHttpClientFactory
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.attendance.AttendanceRepositoryImpl
import com.yapp.plus.data.auth.SessionTokenLocalDataSource
import com.yapp.plus.data.auth.SessionTokenRemoteDataSource
import com.yapp.plus.data.session.SessionScheduleRepositoryImpl
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.session.SessionScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton
import com.yapp.plus.core.network.BuildConfig as NetworkBuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideSessionPersistence(
        @ApplicationContext context: Context,
    ): TokenPersistence =
        SessionTokenLocalDataSource(
            context = context,
            namespace = NetworkBuildConfig.API_BASE_URL,
        )

    @Provides
    @Singleton
    fun provideSessionTokenStore(persistence: TokenPersistence): TokenStore = TokenStore(persistence = persistence)

    @Provides
    @Singleton
    fun provideSessionTokenRefresher(): TokenRefresher =
        SessionTokenRemoteDataSource(ApiHttpClientFactory.createUnauthenticated())

    @Provides
    @Singleton
    fun provideApiHttpClient(
        tokenStore: TokenStore,
        tokenRefresher: TokenRefresher,
    ): HttpClient = ApiHttpClientFactory.create(tokenStore, tokenRefresher)

    @Provides
    @Singleton
    fun provideSessionScheduleRepository(httpClient: HttpClient): SessionScheduleRepository =
        SessionScheduleRepositoryImpl(httpClient)

    @Provides
    @Singleton
    fun provideAttendance(http: HttpClient): AttendanceRepository = AttendanceRepositoryImpl(http)
}
