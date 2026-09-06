package com.nikol.network.httpClient

import com.nikol.network.plugin.YandexAuthPlugin
import com.nikol.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import okhttp3.OkHttpClient
import kotlin.math.pow


internal fun provideYaHttpClient(okHttpClient: OkHttpClient, tManager: TokenManager) =
    HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
        }

        install(Logging) {
            level = LogLevel.HEADERS
            logger = Logger.ANDROID
        }

        install(ContentEncoding) {
            gzip()
            deflate()
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                response.status.value in 500..599
            }
            retryOnExceptionIf { _, cause -> cause is IOException || cause is TimeoutCancellationException }
            exponentialDelay(base = 2.0, maxDelayMs = 10_000)
        }
        install(YandexAuthPlugin) {
            tokenManager = tManager
        }
        defaultRequest {
            url("https://caldav.yandex.ru")
            contentType(ContentType.Application.Xml)
        }
        expectSuccess = true
    }