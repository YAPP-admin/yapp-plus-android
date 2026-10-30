package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.core.network.ApiHttpClientFactory
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.attendance.AttendanceDataSource
import com.yapp.plus.data.attendance.AttendanceRemoteDataSource
import com.yapp.plus.data.attendance.AttendanceRepositoryImpl
import com.yapp.plus.data.attendance.read.AttendanceReadDataSource
import com.yapp.plus.data.attendance.read.AttendanceReadRemoteDataSource
import com.yapp.plus.data.attendance.read.AttendanceReadRepositoryImpl
import com.yapp.plus.data.attendance.team.TeamAttendanceRepositoryImpl
import com.yapp.plus.data.attendance.team.TeamPhotoDataSource
import com.yapp.plus.data.attendance.team.TeamPhotoRemoteDataSource
import com.yapp.plus.data.auth.datasource.auth.AuthDataSource
import com.yapp.plus.data.auth.datasource.auth.AuthRemoteDataSource
import com.yapp.plus.data.auth.datasource.session.SessionProfileDataSource
import com.yapp.plus.data.auth.datasource.session.SessionTokenLocalDataSource
import com.yapp.plus.data.auth.datasource.session.SessionTokenRemoteDataSource
import com.yapp.plus.data.auth.repository.AuthRepositoryImpl
import com.yapp.plus.data.auth.repository.UserProfileRepositoryImpl
import com.yapp.plus.data.notice.NoticeDataSource
import com.yapp.plus.data.notice.NoticeRemoteDataSource
import com.yapp.plus.data.notice.NoticeRepositoryImpl
import com.yapp.plus.data.notification.NotificationDeviceDataSource
import com.yapp.plus.data.notification.NotificationDeviceRemoteDataSource
import com.yapp.plus.data.notification.NotificationDeviceRepositoryImpl
import com.yapp.plus.data.session.SessionScheduleDataSource
import com.yapp.plus.data.session.SessionScheduleRemoteDataSource
import com.yapp.plus.data.session.SessionScheduleRepositoryImpl
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.attendance.TeamAttendanceRepository
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.UserProfileRepository
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notification.NotificationDeviceRepository
import com.yapp.plus.domain.session.SessionScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import javax.inject.Named
import javax.inject.Singleton
import com.yapp.plus.core.network.BuildConfig as NetworkBuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideSessionLocalDataSource(
        @ApplicationContext context: Context,
    ): SessionTokenLocalDataSource =
        SessionTokenLocalDataSource(
            context = context,
            namespace = NetworkBuildConfig.API_BASE_URL,
        )

    @Provides
    fun provideSessionPersistence(source: SessionTokenLocalDataSource): TokenPersistence = source

    @Provides
    fun provideProfileSource(source: SessionTokenLocalDataSource): SessionProfileDataSource = source

    @Provides
    fun provideUserProfileRepository(source: SessionProfileDataSource): UserProfileRepository =
        UserProfileRepositoryImpl(source)

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
    fun provideAuthDataSource(
        @Named(UNAUTHENTICATED_CLIENT) unauthenticatedClient: HttpClient,
        authenticatedClient: HttpClient,
    ): AuthDataSource =
        AuthRemoteDataSource(
            unauthenticatedClient = unauthenticatedClient,
            authenticatedClient = authenticatedClient,
        )

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: AuthDataSource,
        tokenStore: TokenStore,
        profileSource: SessionProfileDataSource,
    ): AuthRepository = AuthRepositoryImpl(dataSource, tokenStore, profileSource)

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

    @Provides
    @Singleton
    fun provideAttendanceReadDataSource(client: HttpClient): AttendanceReadDataSource =
        AttendanceReadRemoteDataSource(client)

    @Provides
    @Singleton
    fun provideAttendanceRead(source: AttendanceReadDataSource): AttendanceReadRepository =
        AttendanceReadRepositoryImpl(source)

    private const val UNAUTHENTICATED_CLIENT = "unauthenticated"

    @Provides
    @Singleton
    @Named("photo-upload")
    fun providePhotoUploadClient(): HttpClient =
        HttpClient(Android) {
            expectSuccess = true
            followRedirects = false
        }

    @Provides
    @Singleton
    fun provideTeamPhotoSource(
        client: HttpClient,
        @Named("photo-upload") uploadClient: HttpClient,
    ): TeamPhotoDataSource = TeamPhotoRemoteDataSource(client, uploadClient)

    @Provides
    @Singleton
    fun provideTeamAttendanceRepository(source: TeamPhotoDataSource): TeamAttendanceRepository =
        TeamAttendanceRepositoryImpl(source)

    @Provides
    @Singleton
    fun provideNotificationDeviceSource(client: HttpClient): NotificationDeviceDataSource =
        NotificationDeviceRemoteDataSource(client)

    @Provides
    @Singleton
    fun provideDeviceRepo(source: NotificationDeviceDataSource): NotificationDeviceRepository =
        NotificationDeviceRepositoryImpl(source)

    @Provides
    @Singleton
    fun provideNoticeSource(client: HttpClient): NoticeDataSource = NoticeRemoteDataSource(client)

    @Provides
    @Singleton
    fun provideNotice(source: NoticeDataSource): NoticeRepository = NoticeRepositoryImpl(source)
}
