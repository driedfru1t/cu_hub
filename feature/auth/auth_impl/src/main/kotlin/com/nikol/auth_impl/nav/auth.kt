package com.nikol.auth_impl.nav

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.auth_impl.screens.CuAuthScreen
import com.nikol.auth_impl.screens.StartAuthScreen
import com.nikol.auth_impl.screens.YandexAuthScreen
import com.nikol.auth_impl.viewModel.AuthComponentViewModel
import com.nikol.auth_impl.viewModel.AuthRouter
import com.nikol.auth_impl.viewModel.AuthViewModel
import com.nikol.di.ext.rememberComponent
import com.nikol.security.CuToken
import com.nikol.security.YandexToken
import com.nikol.viewmodel.LocalViewModelFactory
import com.nikol.viewmodel.daggerViewModel
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import java.time.temporal.TemporalAdjusters.next

inline fun EntryProviderScope<NavKey>.authGraph(
    crossinline onAuthSuccess: () -> Unit
) {
    entry<Auth> {
        val authComponent = rememberComponent { appDep -> AuthComponentViewModel(appDep) }
        val backStack = rememberNavBackStack(Start)
        CompositionLocalProvider(
            LocalViewModelFactory provides authComponent.viewModelFactory()
        ) {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.fillMaxSize(),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Start> { StartAuthScreen({ backStack.add(CuAuth) }) }
                    entry<CuAuth> {

                        val vm = daggerViewModel<AuthViewModel, AuthRouter>(key = "cu") {
                            AuthRouter {
                                backStack.removeLastOrNull()
                                backStack.add(YandexAuth)
                            }
                        }

                        CuAuthScreen(
                            onNavigateBack = { backStack.removeLastOrNull() },
                            onAuthSuccess = {
                                vm.setIntent(AuthIntent.LogIn(cuToken = CuToken(it)))
                            }
                        )
                    }
                    entry<YandexAuth> {
                        val vm = daggerViewModel<AuthViewModel, AuthRouter>(key = "yandex") {
                            AuthRouter { onAuthSuccess() }
                        }
                        val context = LocalContext.current
                        val sdk = remember { YandexAuthSdk.create(YandexAuthOptions(context)) }
                        val launcher = rememberLauncherForActivityResult(sdk.contract) { result ->
                            when (result) {
                                is YandexAuthResult.Success -> vm.setIntent(
                                    AuthIntent.LogIn(yandexToken = YandexToken(result.token.value))
                                )

                                is YandexAuthResult.Cancelled, is YandexAuthResult.Failure -> Unit
                            }
                        }
                        val loginOptions = YandexAuthLoginOptions()

                        YandexAuthScreen(
                            onLoginClick = { launcher.launch(loginOptions) },
                        )
                    }
                }
            )

        }
    }
}