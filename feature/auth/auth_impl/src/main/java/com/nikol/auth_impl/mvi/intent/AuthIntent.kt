package com.nikol.auth_impl.mvi.intent

import com.nikol.security.CuToken
import com.nikol.security.YandexToken
import direct.direct_core.DirectIntent

sealed interface AuthIntent : DirectIntent {
    data class LogIn(val cuToken: CuToken? = null, val yandexToken: YandexToken? = null) :
        AuthIntent

    data object NavNext : AuthIntent
}