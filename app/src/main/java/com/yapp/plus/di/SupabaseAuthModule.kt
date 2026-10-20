package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.BuildConfig
import com.yapp.plus.data.auth.AuthDataSource
import com.yapp.plus.data.auth.AuthRepositoryImpl
import com.yapp.plus.data.auth.SupabaseAuthDataSource
import com.yapp.plus.data.auth.SupabaseTokenLocalDataSource
import com.yapp.plus.domain.auth.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseAuthModule {
    @Provides
    @Singleton
    @SupabaseAuthClient
    fun provideSupabaseAuthHttpClient(): HttpClient =
        HttpClient(Android) {
            followRedirects = false
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 30_000
            }
        }

    @Provides
    @Singleton
    fun provideAuthDataSource(
        @SupabaseAuthClient httpClient: HttpClient,
        @ApplicationContext context: Context,
    ): AuthDataSource =
        SupabaseAuthDataSource(
            httpClient = httpClient,
            projectUrl = BuildConfig.SUPABASE_URL,
            publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            tokenLocalDataSource = SupabaseTokenLocalDataSource(context, BuildConfig.SUPABASE_URL),
        )

    @Provides
    @Singleton
    fun provideAuth(dataSource: AuthDataSource): AuthRepository = AuthRepositoryImpl(dataSource)
}
