package com.nikol.network.engine

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal fun provideOkHttpEngine(): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .followRedirects(true)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    return okHttpClient
}