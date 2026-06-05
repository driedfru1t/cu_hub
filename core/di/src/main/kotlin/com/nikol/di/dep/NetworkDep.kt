package com.nikol.di.dep

import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.network.di.qualifers.YandexHttpClient
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

interface NetworkDep : NetworkCuDep, NetworkYandexDep

interface JsonDep {
    fun json(): Json
}

interface NetworkCuDep : JsonDep {
    @CuHttpClient
    fun cuHttpClient(): HttpClient
}

interface NetworkYandexDep : JsonDep {
    @YandexHttpClient
    fun yandexHttpClient(): HttpClient
}