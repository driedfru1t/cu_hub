package com.nikol.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

// эта штука нужна чтобы мы прокидывали свой ViewModelFactory где есть фабрики всех viewModel конкретно этого компонента
val LocalViewModelFactory =
    compositionLocalOf<ViewModelProvider.Factory> { error("не запровайжена фабрика") }

@Composable
inline fun <reified VM : RouterViewModel<*, *, *, R>, reified R : Router> daggerViewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory,
    crossinline routerFactory: () -> R
): VM {
    val vm: VM = viewModel(key = key, factory = factory)
    DisposableEffect(vm) {
        val router = routerFactory()
        vm.attachRouter(router)
        onDispose { vm.detachRouter() }
    }
    return vm
}


@Composable
inline fun <reified VM : RouterViewModel<*, *, *, R>, reified R : Router> daggerViewModel(
    key: String? = null,
    crossinline routerFactory: () -> R
): VM {
    val factory = LocalViewModelFactory.current
    val vm: VM = viewModel(key = key, factory = factory)
    DisposableEffect(vm) {
        val router = routerFactory()
        vm.attachRouter(router)
        onDispose { vm.detachRouter() }
    }
    return vm
}

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(
    key: String? = null,
): VM {
    val factory = LocalViewModelFactory.current
    return viewModel(key = key, factory = factory)
}


