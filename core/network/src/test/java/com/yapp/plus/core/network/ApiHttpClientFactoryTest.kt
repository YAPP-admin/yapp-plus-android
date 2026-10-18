package com.yapp.plus.core.network

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class ApiHttpClientFactoryTest {
    private val apiOrigin =
        URI
            .create("https://api.example.test")

    @Test
    fun `relative request attaches authorization`() {
        assertTrue(ApiHttpClientFactory.shouldAttachAuthorization(URLBuilder(), apiOrigin))
    }

    @Test
    fun `request to API origin attaches authorization`() {
        val requestUrl =
            URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = apiOrigin.host
                port = 443
            }

        assertTrue(ApiHttpClientFactory.shouldAttachAuthorization(requestUrl, apiOrigin))
    }

    @Test
    fun `request to another origin does not attach authorization`() {
        val requestUrl =
            URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = "storage.example.com"
                port = 443
            }

        assertFalse(ApiHttpClientFactory.shouldAttachAuthorization(requestUrl, apiOrigin))
    }
}
