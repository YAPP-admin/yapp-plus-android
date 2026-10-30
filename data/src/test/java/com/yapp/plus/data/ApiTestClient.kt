package com.yapp.plus.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun apiTestClient(engine: MockEngine) =
    HttpClient(engine) {
        expectSuccess = true
        install(DefaultRequest) {
            url("https://api.test/api/")
            header("Authorization", "Bearer test-access")
        }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
