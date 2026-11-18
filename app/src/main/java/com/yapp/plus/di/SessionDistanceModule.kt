package com.yapp.plus.di

import android.content.Context
import com.yapp.plus.data.session.DeviceSessionDistanceDataSource
import com.yapp.plus.data.session.SessionDistanceRepositoryImpl
import com.yapp.plus.domain.session.SessionDistanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionDistanceModule {
    @Provides
    fun provideSessionDistanceRepository(
        @ApplicationContext context: Context,
    ): SessionDistanceRepository {
        val dataSource = DeviceSessionDistanceDataSource(context)
        return SessionDistanceRepositoryImpl(dataSource)
    }
}
