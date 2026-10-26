package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.core.network.ApiHttpClientFactory
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.attendance.AttendanceDataSource
import com.yapp.plus.data.attendance.AttendanceRemoteDataSource
import com.yapp.plus.data.attendance.AttendanceRepositoryImpl
import com.yapp.plus.data.auth.datasource.session.SessionTokenLocalDataSource
import com.yapp.plus.data.auth.datasource.session.SessionTokenRemoteDataSource
import com.yapp.plus.data.session.SessionScheduleDataSource
import com.yapp.plus.data.session.SessionScheduleRemoteDataSource
import com.yapp.plus.data.session.SessionScheduleRepositoryImpl
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.session.SessionScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named
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
    fun provideSessionTokenStore(tokenPersistence: TokenPersistence): TokenStore =
        TokenStore(persistence = tokenPersistence)

    @Provides
    @Singleton
    @Named(UNAUTHENTICATED_CLIENT)
    fun provideUnauthenticatedClient(): HttpClient = ApiHttpClientFactory.createUnauthenticated()

    @Provides
    @Singleton
    fun provideSessionTokenRefresher(
        @Named(UNAUTHENTICATED_CLIENT) httpClient: HttpClient,
    ): TokenRefresher = SessionTokenRemoteDataSource(httpClient)

    @Provides
    @Singleton
    fun provideApiHttpClient(
        tokenStore: TokenStore,
        tokenRefresher: TokenRefresher,
    ): HttpClient = ApiHttpClientFactory.create(tokenStore, tokenRefresher)

    @Provides
    @Singleton
    fun provideSessionScheduleDataSource(client: HttpClient): SessionScheduleDataSource =
        SessionScheduleRemoteDataSource(client)

    @Provides
    @Singleton
    fun provideSessionSchedule(source: SessionScheduleDataSource): SessionScheduleRepository =
        SessionScheduleRepositoryImpl(source)

    @Provides
    @Singleton
    fun provideAttendanceDataSource(client: HttpClient): AttendanceDataSource =
        AttendanceRemoteDataSource(httpClient = client)

    @Provides
    @Singleton
    fun provideAttendance(dataSource: AttendanceDataSource): AttendanceRepository =
        AttendanceRepositoryImpl(dataSource = dataSource)

    private const val UNAUTHENTICATED_CLIENT = "unauthenticated"
}
