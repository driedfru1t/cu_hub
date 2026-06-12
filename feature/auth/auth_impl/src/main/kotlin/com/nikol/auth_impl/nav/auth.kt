package com.nikol.auth_impl.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.screens.CuAuthScreen
import com.nikol.auth_impl.screens.StartAuthScreen
import com.nikol.auth_impl.screens.YandexAuthScreen
import com.nikol.auth_impl.viewModel.AuthComponentViewModel
import com.nikol.di.ext.rememberComponent
import com.nikol.viewmodel.LocalViewModelFactory

inline fun EntryProviderScope<NavKey>.authGraph(
    crossinline onAuthSuccess: () -> Unit
) {
    entry<Auth> {
        val authComponent = rememberComponent { appDep -> AuthComponentViewModel(appDep) }
        val backStack = rememberNavBackStack(Start)
        CompositionLocalProvider(
            LocalViewModelFactory provides authComponent.viewModelFactory()
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(paddingValues),
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<Start> { StartAuthScreen { onAuthSuccess() } }
                        entry<CuAuth> {
                            CuAuthScreen {
                                backStack.removeLastOrNull()
                                backStack.add(YandexAuth)
                            }
                        }
                        entry<YandexAuth> {
                            YandexAuthScreen {
                                backStack.apply {
                                    clear()
                                    add(Start)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}