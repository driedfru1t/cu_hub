package com.nikol.network.plugin

import com.nikol.security.TokenManager
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

class AuthConfig {
    var tokenManager: TokenManager? = null
}

val CuAuthPlugin = createClientPlugin("CuAuthPlugin", ::AuthConfig) {
    val tokenManager = pluginConfig.tokenManager
        ?: error("TokenManager must be provided for CuAuthPlugin")

    on(Send) { request ->
//        val initialToken = tokenManager.getCuToken()
//        initialToken?.let {
//            request.headers.append(HttpHeaders.Cookie, "bff.cookie=${it.token}")
//        }


        request.headers.append(
            HttpHeaders.Cookie,
            "bff.cookie=CfDJ8I4ChYQ3CfRGhjQ6ve1DmbbSX9DX0YyKChpAYsEVcWiSiGE%2FUUz7gZ%2F%2Fq1WexSEhG%2BFPbZVTHctSnAPSGTnv4RsCJVd3WZNmbMSsR3yX2QWP0xtiUoJBBSRGPP29ks6JDC9lW7SdoQowV%2BsQSMriPiYviS%2BTFJKXV%2FcpMPsZATgQ"
        )
        proceed(request)
    }
}

val YandexAuthPlugin = createClientPlugin("YandexAuthPlugin", ::AuthConfig) {
    val tokenManager =
        pluginConfig.tokenManager ?: error("TokenManager must be provided for YandexAuthPlugin")
    on(Send) { request ->
//        val initialToken = tokenManager.getYandexToken()
//        initialToken?.let {
//            request.headers.append(HttpHeaders.Authorization, "OAuth ${it.token}")
//        }


        request.headers.append(
            HttpHeaders.Authorization,
            "OAuth y0__wgBEMKhhaaq94ACGLjlQiDOm_-oGKazUXqmtBcKcBNqn88U0UQO6kT9"
        )
        proceed(request)
    }
}
