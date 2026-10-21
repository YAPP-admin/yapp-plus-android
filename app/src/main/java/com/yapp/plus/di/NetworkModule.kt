package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.BuildConfig
import com.yapp.plus.core.network.ApiHttpClientFactory
import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.attendance.AttendanceDataSource
import com.yapp.plus.data.attendance.AttendanceRepositoryImpl
import com.yapp.plus.data.attendance.SupabaseAttendanceDataSource
import com.yapp.plus.data.auth.AuthDataSource
import com.yapp.plus.data.auth.AuthTokenLocalDataSource
import com.yapp.plus.data.auth.SessionTokenLocalDataSource
import com.yapp.plus.data.auth.SessionTokenRemoteDataSource
import com.yapp.plus.data.notice.NoticeDataSource
import com.yapp.plus.data.notice.NoticeRepositoryImpl
import com.yapp.plus.data.notice.SupabaseNoticeDataSource
import com.yapp.plus.data.profile.ProfileDataSource
import com.yapp.plus.data.profile.ProfileRepositoryImpl
import com.yapp.plus.data.profile.SupabaseProfileDataSource
import com.yapp.plus.data.session.SessionScheduleDataSource
import com.yapp.plus.data.session.SessionScheduleRepositoryImpl
import com.yapp.plus.data.session.SupabaseSessionScheduleDataSource
import com.yapp.plus.data.supabase.SupabaseRestClient
import com.yapp.plus.domain.attendance.AttendanceRepository
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.profile.ProfileRepository
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
    fun provideSessionScheduleDataSource(client: SupabaseRestClient): SessionScheduleDataSource =
        SupabaseSessionScheduleDataSource(client)

    @Provides
    @Singleton
    fun provideSessionScheduleRepository(dataSource: SessionScheduleDataSource): SessionScheduleRepository =
        SessionScheduleRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideAttendanceDataSource(client: SupabaseRestClient): AttendanceDataSource =
        SupabaseAttendanceDataSource(client)

    @Provides
    @Singleton
    fun provideAttendance(dataSource: AttendanceDataSource): AttendanceRepository = AttendanceRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideSupabaseRestClient(
        @SupabaseAuthClient httpClient: HttpClient,
        tokens: AuthTokenLocalDataSource,
        authDataSource: AuthDataSource,
    ): SupabaseRestClient =
        SupabaseRestClient(
            httpClient = httpClient,
            projectUrl = BuildConfig.SUPABASE_URL,
            publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            tokenLocalDataSource = tokens,
            authDataSource = authDataSource,
        )

    @Provides
    @Singleton
    fun provideNoticeDataSource(client: SupabaseRestClient): NoticeDataSource =
        SupabaseNoticeDataSource(client = client)

    @Provides
    @Singleton
    fun provideNotices(dataSource: NoticeDataSource): NoticeRepository = NoticeRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideProfileDataSource(client: SupabaseRestClient): ProfileDataSource =
        SupabaseProfileDataSource(client)

    @Provides
    @Singleton
    fun provideProfile(dataSource: ProfileDataSource): ProfileRepository =
        ProfileRepositoryImpl(dataSource)
}
