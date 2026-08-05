package com.nikol.network.di.modules

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.nikol.network.SecureImageInterceptor
import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.network.engine.provideOkHttpEngine
import com.nikol.network.httpClient.provideCoilHttpClient
import com.nikol.network.httpClient.provideLmsHttpClient
import com.nikol.network.httpClient.provideYaHttpClient
import com.nikol.security.TokenManager
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.io.File
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
    @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.CU)
    fun provideCuHttpClient(
        okHttpClient: OkHttpClient,
        json: Json,
        tokenManager: TokenManager
    ): HttpClient {
        return provideLmsHttpClient(okHttpClient, json, tokenManager)
    }

    @Singleton
    @Provides
    @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.Yandex)
    fun provideYandexHttpClient(okHttpClient: OkHttpClient, tokenManager: TokenManager): HttpClient {
        return provideYaHttpClient(okHttpClient, tokenManager)
    }

    @Singleton
    @Provides
    @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.Coil)
    fun provideImageHttpClient(okHttpClient: OkHttpClient, json: Json): HttpClient {
        return provideCoilHttpClient(okHttpClient, json)
    }

    @Singleton
    @Provides
    fun provideImageLoader(
        context: Context,
        secureImageInterceptor: SecureImageInterceptor,
        @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.Coil) coilHttpClient: HttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(secureImageInterceptor)
                add(KtorNetworkFetcherFactory(httpClient = { coilHttpClient }))
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .weakReferencesEnabled(true)
                    .build()
            }
            .build()
    }

}