package com.nikol.network.httpClient

import com.nikol.network.plugin.CuAuthPlugin
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
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import kotlin.math.pow

internal fun provideLmsHttpClient(
    okHttpClient: OkHttpClient, json: Json, tManager: TokenManager
) = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpClient
    }
    install(plugin = ContentNegotiation) {
        json(json = json)
    }
    install(Resources)

    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.ANDROID
    }
    install(CuAuthPlugin) {
        tokenManager = tManager
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
    defaultRequest {
        url("https://my.centraluniversity.ru/api/micro-lms/")
        header(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.5 Safari/605.1.15"
        )
        header("Accept", "application/json, text/plain, */*")
        header("Accept-Language", "ru")

        header("Sec-Fetch-Dest", "empty")
        header("Sec-Fetch-Mode", "cors")
        header("Sec-Fetch-Site", "same-origin")

    }
    expectSuccess = true
}