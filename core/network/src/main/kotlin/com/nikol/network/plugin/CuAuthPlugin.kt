package com.nikol.network.plugin

import com.nikol.security.TokenManager
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

class CuAuthConfig {
    var tokenManager: TokenManager? = null
}

val CuAuthPlugin = createClientPlugin("CuAuthPlugin", ::CuAuthConfig) {
    val tokenManager = pluginConfig.tokenManager
        ?: error("TokenManager must be provided for CuAuthPlugin")

    on(Send) { request ->
        val initialToken = tokenManager.getCuToken()
        initialToken?.let {
            request.headers.append(HttpHeaders.Cookie, "bff.cookie=${it.token}")
        }
        proceed(request)
    }
}
