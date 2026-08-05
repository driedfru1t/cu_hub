package com.nikol.di.dep

import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

interface NetworkDep : NetworkCuDep, NetworkYandexDep

interface JsonDep {
    fun json(): Json
}

interface NetworkCuDep : JsonDep, DispatcherDep {
    @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.CU)
    fun cuHttpClient(): HttpClient
}

interface NetworkYandexDep : JsonDep, DispatcherDep {
    @com.nikol.network.di.qualifers.HttpClient(CuHttpClient.Yandex)
    fun yandexHttpClient(): HttpClient
}