package com.nikol.network.di.modules

import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.network.httpClient.provideOkHttpClient
import com.nikol.network.engine.provideOkHttpEngine
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpEngine(): OkHttpClient {
        return provideOkHttpEngine()
    }

    @Singleton
    @Provides
    @CuHttpClient
    fun provideHttpclient(okHttpClient: OkHttpClient): HttpClient {
        return provideOkHttpClient(okHttpClient)
    }
}