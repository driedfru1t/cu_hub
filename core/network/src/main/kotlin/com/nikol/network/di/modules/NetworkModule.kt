package com.nikol.network.di.modules

import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.network.di.qualifers.YandexHttpClient
import com.nikol.network.engine.provideOkHttpEngine
import com.nikol.network.httpClient.provideLmsHttpClient
import com.nikol.network.httpClient.provideYaHttpClient
import com.nikol.security.TokenManager
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpEngine(): OkHttpClient {
        return provideOkHttpEngine()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            explicitNulls = false
            prettyPrint = true
        }
    }

    @Singleton
    @Provides
    @CuHttpClient
    fun provideCuHttpClient(
        okHttpClient: OkHttpClient,
        json: Json,
        tokenManager: TokenManager
    ): HttpClient {
        return provideLmsHttpClient(okHttpClient, json, tokenManager)
    }

    @Singleton
    @Provides
    @YandexHttpClient
    fun provideYandexHttpClient(okHttpClient: OkHttpClient, json: Json): HttpClient {
        return provideYaHttpClient(okHttpClient, json)
    }

}