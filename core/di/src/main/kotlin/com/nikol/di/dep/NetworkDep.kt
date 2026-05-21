package com.nikol.di.dep

import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient

interface NetworkDep : NetworkCuDep


interface NetworkCuDep {
    @CuHttpClient
    fun cuHttpClient(): HttpClient
}