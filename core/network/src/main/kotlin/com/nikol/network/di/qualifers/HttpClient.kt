package com.nikol.network.di.qualifers

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class HttpClient(val client: CuHttpClient)

enum class CuHttpClient {
    Yandex,
    CU,

    Coil
}