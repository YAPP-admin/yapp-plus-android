package com.yapp.plus.data.supabase

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.data.auth.FakeAuthTokenLocalDataSource
import com.yapp.plus.data.auth.SupabaseAuthDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler

class SupabaseTestFixture(
    handler: MockRequestHandler,
    tokens: SessionTokenRecord =
        SessionTokenRecord(accessToken = "access-secret", refreshToken = "refresh-secret"),
) : AutoCloseable {
    val storage = FakeAuthTokenLocalDataSource(tokens)
    private val httpClient = HttpClient(MockEngine(handler))
    private val projectUrl = "https://project.supabase.co"
    private val publishableKey = "sb_publishable_test"
    val client =
        SupabaseRestClient(
            httpClient = httpClient,
            projectUrl = projectUrl,
            publishableKey = publishableKey,
            tokenLocalDataSource = storage,
            authDataSource =
                SupabaseAuthDataSource(
                    httpClient = httpClient,
                    projectUrl = projectUrl,
                    publishableKey = publishableKey,
                    tokenLocalDataSource = storage,
                ),
        )

    override fun close() = httpClient.close()
}
