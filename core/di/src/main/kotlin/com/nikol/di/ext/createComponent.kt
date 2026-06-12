package com.nikol.di.ext

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nikol.di.dep.AppDep

abstract class ComponentViewModel<T>(
    val component: T
) : ViewModel() {

    init {
        Log.d("DI", "${component!!::class.java.simpleName} успешно создан")
    }

    override fun onCleared() {
        Log.d("DI", "${component!!::class.java.simpleName} успешно уничтожен")
    }
}

@Composable
inline fun <reified VM : ComponentViewModel<C>, reified C> rememberComponent(
    key: String? = null,
    crossinline creator: (AppDep) -> VM
): C {
    val appComponent = LocalAppDep.current

    val factory = remember(appComponent) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(appComponent) as T
            }
        }
    }

    val componentViewModel: VM = viewModel(key = key, factory = factory)
    return componentViewModel.component
}