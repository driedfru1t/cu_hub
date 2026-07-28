package com.nikol.network.httpClient

import com.nikol.network.plugin.YandexAuthPlugin
import com.nikol.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import kotlin.math.pow

internal fun provideYaHttpClient(okHttpClient: OkHttpClient, tManager: TokenManager) =
    HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.ANDROID
        }
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                response.status.value in 500..599
            }
            retryOnExceptionIf { _, cause -> cause is IOException || cause is TimeoutCancellationException }


            delayMillis { retry ->
                1000L * 2.0.pow(retry.toDouble()).toLong()
            }
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