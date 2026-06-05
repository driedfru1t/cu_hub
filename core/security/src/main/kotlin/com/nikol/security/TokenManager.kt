package com.nikol.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nikol.prefs.qualifers.TokenDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface TokenManager {
    val cuTokenFlow: Flow<CuToken?>
    val yandexTokenFlow: Flow<YandexToken?>

    suspend fun getCuToken(): CuToken?
    suspend fun getYandexToken(): YandexToken?

    suspend fun saveCuToken(token: CuToken)
    suspend fun saveYandexToken(token: YandexToken)

    suspend fun clearTokens()
}

@Singleton
class TokenManagerImpl @Inject constructor(
    private val keyStore: KeyStore,
    @param:TokenDataStore private val dataStore: DataStore<Preferences>,
) : TokenManager {

    private companion object {
        val CU_TOKEN_KEY = stringPreferencesKey("cu_token")
        val YANDEX_TOKEN_KEY = stringPreferencesKey("yandex_token")
    }

    override val cuTokenFlow: Flow<CuToken?> = dataStore.data
        .map { prefs -> prefs[CU_TOKEN_KEY]?.let { CuToken(keyStore.decrypt(it)) } }

    override val yandexTokenFlow: Flow<YandexToken?> = dataStore.data
        .map { prefs -> prefs[YANDEX_TOKEN_KEY]?.let { YandexToken(keyStore.decrypt(it)) } }

    override suspend fun getCuToken(): CuToken? = cuTokenFlow.first()

    override suspend fun getYandexToken(): YandexToken? = yandexTokenFlow.first()

    override suspend fun saveCuToken(token: CuToken) {
        dataStore.edit { it[CU_TOKEN_KEY] = keyStore.encrypt(token.token) }
    }

    override suspend fun saveYandexToken(token: YandexToken) {
        dataStore.edit { it[YANDEX_TOKEN_KEY] = keyStore.encrypt(token.token) }
    }

    override suspend fun clearTokens() {
        dataStore.edit { it.clear() }
    }
}