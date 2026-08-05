package com.nikol.auth_impl.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.auth_impl.viewModel.AuthRouter
import com.nikol.auth_impl.viewModel.AuthViewModel
import com.nikol.security.YandexToken
import com.nikol.viewmodel.daggerViewModel
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

@Composable
fun YandexAuthScreen(
    next: () -> Unit
) {
    val vm = daggerViewModel<AuthViewModel, AuthRouter>(key = "yandex") { AuthRouter { next() } }
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
    val loginOptions =  YandexAuthLoginOptions()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { launcher.launch(loginOptions) }) {
            Text("Зарегаться через яндекс")
        }
    }
}