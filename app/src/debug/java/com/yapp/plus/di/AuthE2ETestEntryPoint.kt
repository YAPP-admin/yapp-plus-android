package com.yapp.plus.di

import com.yapp.plus.core.network.auth.TokenStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthE2ETestEntryPoint {
    @Named("unauthenticated")
    fun unauthenticatedClient(): HttpClient

    fun tokenStore(): TokenStore
}
