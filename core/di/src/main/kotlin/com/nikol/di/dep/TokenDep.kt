package com.nikol.di.dep

import com.nikol.security.TokenManager

interface TokenDep {
    fun TokenManager(): TokenManager
}