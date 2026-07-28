package com.nikol.network.httpClient

import com.nikol.network.plugin.CoilImageLoggerPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

fun provideCoilHttpClient(okHttpClient: OkHttpClient, json: Json) = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpClient
    }
    install(plugin = ContentNegotiation) {
        json(json = json)
    }

    //install(CoilImageLoggerPlugin)
    install(Logging) {
        level = LogLevel.INFO
        logger = Logger.ANDROID
    }
}