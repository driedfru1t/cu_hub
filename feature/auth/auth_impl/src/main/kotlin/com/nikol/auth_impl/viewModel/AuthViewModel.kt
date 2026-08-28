package com.nikol.auth_impl.viewModel

import android.util.Log
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.security.TokenManager
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.DirectState
import direct.direct_core.on
import javax.inject.Inject

fun interface AuthRouter : Router {
    fun toYandexAuth()
}

class AuthViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : RouterViewModel<AuthIntent, DirectState, DirectEffect, AuthRouter>() {
    override fun createInitialState(): DirectState {
        return object : DirectState {}
    }

    override fun handleIntents() = intents {
        on<AuthIntent.LogIn> { intent ->
            intent.yandexToken?.let {
                tokenManager.saveYandexToken(it)
            }
            intent.cuToken?.let {
                tokenManager.saveCuToken(it)
            }
            if (intent.yandexToken != null || intent.cuToken != null) {
                setIntent(AuthIntent.NavNext)
            }
        }
        onNavigate<AuthIntent.NavNext>(true) { toYandexAuth() }
    }
}