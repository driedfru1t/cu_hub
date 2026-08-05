package com.nikol.network.plugin

import android.util.Log
import io.ktor.client.plugins.api.createClientPlugin

val CoilImageLoggerPlugin = createClientPlugin("CoilImageLogger") {
    onRequest { request, _ ->
        Log.d("CoilNetwork", "Request: ${request.url} \n METHOD: ${request.method}")
    }
    onResponse { response ->
        if (response.status.value in 200..299) {
            Log.d("CoilNetwork", "RESPONSE: ${response.status.value}")
        } else {
            Log.e(
                "CoilNetwork",
                "RESPONSE: FAILED (${response.status.value} ${response.status.description})"
            )
        }
    }
}