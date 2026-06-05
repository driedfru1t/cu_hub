package com.nikol.auth_impl.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.auth_impl.di.AuthComponent
import com.nikol.auth_impl.di.DaggerAuthComponent
import com.nikol.di.dep.AppDep

class AuthComponentViewModel(
    appComponent: AppDep
) : ViewModel() {
    val authComponent =
        DaggerAuthComponent.factory().create(appComponent, appComponent, appComponent)

    init {
        Log.d("DI", "AuthComponent успешно создан")
    }

    override fun onCleared() {
        Log.d("DI", "AuthComponent успешно уничтожен")
    }
}

class AuthComponentFactory(
    private val appComponent: AppDep
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthComponentViewModel(appComponent) as T
    }
}