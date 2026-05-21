package com.nikol.security.di

import com.nikol.security.KeyStore
import com.nikol.security.TokenManager
import com.nikol.security.TokenManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface TokenModule {
    @Binds
    fun bindTokenManager(tokenManagerImpl: TokenManagerImpl): TokenManager

    companion object {
        @Provides
        @Singleton
        fun provideKeyStore(): KeyStore {
            return KeyStore()
        }
    }
}