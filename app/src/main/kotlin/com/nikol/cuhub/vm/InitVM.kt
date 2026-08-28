package com.nikol.cuhub.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nikol.security.TokenManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


sealed interface ConfigState {
    data object Loading : ConfigState
    data class Success(
        override val isAuthSuccess: Boolean
    ) : ConfigState

    val shouldKeepSplashScreen: Boolean
        get() = this is Loading

    val isAuthSuccess: Boolean
        get() = false
}

class InitVM(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val result = checkAuth()
            _state.tryEmit(ConfigState.Success(result))
        }
    }


    private suspend fun checkAuth(): Boolean {
        return tokenManager.getCuToken() != null &&
                tokenManager.getYandexToken() != null
    }

    class Factory @Inject constructor(
        private val tokenManager: TokenManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InitVM(tokenManager) as T
        }
    }
}